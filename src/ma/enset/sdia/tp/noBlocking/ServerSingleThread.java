package ma.enset.sdia.tp.noBlocking;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ServerSingleThread {
    public static void main(String[] args) throws Exception {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress("localhost", 12345));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Le serveur Single thread est démarré ... .");
        while (true) {
            int select = selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()) {
                    hundleAccept(selectionKey,selector);
                } else if (selectionKey.isReadable()) {
                    hundleReadWrite(selectionKey,selector);
                }
                iterator.remove();
            }
        }

    }
    public static void hundleAccept(SelectionKey selectionKey, Selector selector)throws Exception{
        ServerSocketChannel serverSocketChannel= (ServerSocketChannel) selectionKey.channel();
        SocketChannel socketChannel=serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector,SelectionKey.OP_READ);
        System.out.println(String.format("new connection from %S " , socketChannel.getRemoteAddress().toString()));

    }
    public static void hundleReadWrite(SelectionKey selectionKey, Selector selector) throws Exception {
    SocketChannel socketChannel= (SocketChannel) selectionKey.channel();
    ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
    int dataSize=socketChannel.read(byteBuffer);
    if (dataSize == -1){
        System.out.println(String.format("the client %s has been disconnected", socketChannel.getRemoteAddress()));
    }
    String request =new String(byteBuffer.array()).trim();
    System.out.println(String.format("new request %s from %s", request,socketChannel.getRemoteAddress().toString()));
    String response = new StringBuffer(request).reverse().toString().toUpperCase();
    ByteBuffer byteBufferresponse=ByteBuffer.allocate(1024);
    byteBufferresponse.put(response.getBytes());
    byteBufferresponse.flip();
    socketChannel.write(byteBufferresponse);

}
}
