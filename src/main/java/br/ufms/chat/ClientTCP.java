package br.ufms.chat;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ClientTCP {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 1234);

        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        // Lê a chave enviada pelo servidor e inicializa
        String encodedKey = inFromServer.readLine();
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        SecretKey secretKey = new SecretKeySpec(decodedKey, "DES");

        // Thread para receber as mensagens do servidor
        new Thread(()->{
            try{
                Cipher cipherDESDecriptor = Cipher.getInstance("DES/ECB/PKCS5Padding");
                cipherDESDecriptor.init(Cipher.DECRYPT_MODE, secretKey);
                String serverResponse;

                while ((serverResponse = inFromServer.readLine()) != null){
                    byte[] encryptedMessage = Base64.getDecoder().decode(serverResponse);
                    byte[] decryptedMessage = cipherDESDecriptor.doFinal(encryptedMessage);
                    System.out.println(new String(decryptedMessage));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();

        // Pegar input do usuário, criptografar e enviar
        Cipher cipherDesEncriptor = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipherDesEncriptor.init(Cipher.ENCRYPT_MODE, secretKey);
        while(true){
            String clientMessage = inFromUser.readLine();
            byte[] encryptedMessage = cipherDesEncriptor.doFinal(clientMessage.getBytes());
            String encodedMessage = Base64.getEncoder().encodeToString(encryptedMessage);
            outToServer.writeBytes(encodedMessage+"\n");
        }
    }
}
