    package ma.enset.sdia.tp.noBlocking;

    import java.io.IOException;
    import java.net.InetSocketAddress;
    import java.nio.ByteBuffer;
    import java.nio.channels.SocketChannel;
    import java.util.Scanner;

    public class ClientSinglethread {
        public static void main(String[] args) throws IOException {


            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.01",12345));
            System.out.println("le client est connecté");
            Scanner scanner=new Scanner(System.in);
            new Thread(()->{
                while (true){
                    ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
                    try {
                        socketChannel.read(byteBuffer);
                        String response=new String(byteBuffer.array()).trim();
                        if(response.length()>0){
                            System.out.println("Response => "+response);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();

            while(true){
                String request = scanner.nextLine();
                ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
                byteBuffer.put(request.getBytes());
                byteBuffer.flip();
                int bytesWritten = socketChannel.write(byteBuffer);
                System.out.println(String.format("sending %s bytes",bytesWritten));
            }
        }
    }
