package ma.enset.sdia.tp.blocking;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MultiThreadChatServer extends Thread{
    List<Conversation> conversations=new ArrayList<>();
    int clientCount;
    public static void main(String[] args) {
        new MultiThreadChatServer().start();
    }

    @Override
    public void run(){
        System.out.println("The server is starting using the port number 1234");
        try {
            ServerSocket serverSocket=new ServerSocket(1234);
            while (true){
                Socket socket=serverSocket.accept();
                ++clientCount;
                Conversation conversation=new Conversation(socket,clientCount);
                conversations.add(conversation);
                conversation.start();
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    class Conversation extends Thread{
        private Socket socket;
        private int clientId;
        public Conversation(Socket socket,int clientId){
            this.socket=socket;
            this.clientId=clientId;
        }
        @Override
        public void run(){
            try {
                InputStream is= socket.getInputStream();
                InputStreamReader isr=new InputStreamReader(is);
                BufferedReader br=new BufferedReader(isr);
                OutputStream os=socket.getOutputStream();
                PrintWriter pw=new PrintWriter(os,true);
                pw.println("  Welcome, your ID is => "+clientId);
                while (true){
                    String request=br.readLine();
                    List<Integer> clientsTo=new ArrayList<>();
                    String messages = null;
                    if(request.contains("=>")){
                        String[] items=request.split("=>");
                        String clients=items[0];
                        messages=items[1];
                        if(clients.contains(",")){
                            String[] clientIds=clients.split(",");
                            for(String id:clientIds){
                                clientsTo.add(Integer.parseInt(id));
                            }
                        }
                        else{
                            clientsTo.add(Integer.parseInt(clients));
                            messages=request;
                        }
                    } else{
                        clientsTo=conversations.stream().map(c->c.clientId).collect(Collectors.toList());
                    }
                    brodcastMessage(request,this,clientsTo);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void brodcastMessage(String message,Conversation c,List<Integer> clients) throws IOException {
        for(Conversation conversation : conversations){
            if(conversation!=c && clients.contains(conversation.clientId)) {
                Socket socket = conversation.socket;
                OutputStream outputStream = socket.getOutputStream();
                PrintWriter printWriter = new PrintWriter(outputStream, true);
                printWriter.println(message);
            }
        }
    }
}