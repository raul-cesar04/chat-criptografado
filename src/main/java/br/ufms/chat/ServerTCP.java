package br.ufms.chat;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ServerTCP {
    // Clientes Conectados no chat para receberem as mensagens
    private static final List<ClientHandler> clients = new ArrayList<>();
    public static void main(String[] args) throws Exception {
        // Socket Server
        ServerSocket welcomeSocket = new ServerSocket(1234);

        // Crypto - Inicializa a chave DES
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        SecretKey key = keyGen.generateKey();

        while (true){
            Socket connectionSocket = welcomeSocket.accept();

            // Conecta com o cliente e inicializa uma thread para receber suas mensagens
            ClientHandler clientHandler = new ClientHandler(connectionSocket, key);
            clients.add(clientHandler);
            new Thread(clientHandler).start();
        }
    }

    private static void broadcast(String message, ClientHandler sender) throws IOException{
        for(ClientHandler clientHandler : clients){
            if(clientHandler.equals(sender)) continue;
            clientHandler.sendMessage(message);
        }
    }

    private static class ClientHandler implements Runnable{
        private SecretKey key;
        private Socket socket;

        // Não usado por conta da criptografia.
        private String username;

        private BufferedReader inFromClient;
        private DataOutputStream outToClient;

        public ClientHandler(Socket socket, SecretKey key) throws IOException {
            this.socket = socket;
            this.key = key;

            username = NameGenerator.generateName();
            inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outToClient = new DataOutputStream(socket.getOutputStream());

            // Envia a chave para o cliente criptografar/descriptografar as mensagens
            outToClient.writeBytes(getKeyEncodedString()+"\n");

        }

        @Override
        public void run() {
            try{
                String inLine;

                // Recebe as mensagens dos clientes
                while((inLine = inFromClient.readLine()) != null){
                    broadcast(inLine, this);
                }

                clients.remove(this);
                inFromClient.close();
                outToClient.close();
            }catch (SocketException e){
                System.out.println("Socket "+socket+" closed.");
                clients.remove(this);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        private void sendMessage(String message) throws IOException {
            outToClient.writeBytes(message+"\n");
        }

        private String getKeyEncodedString(){
            return Base64.getEncoder().encodeToString(key.getEncoded());
        }
    }
}

