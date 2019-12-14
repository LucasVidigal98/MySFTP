
package main;

import java.io.IOException;
import java.util.Arrays;
import root.Root;
import user.User;

public class Main {
    
    public static void main(String[] args) throws IOException, ClassNotFoundException, Exception {
        
        System.out.println(Arrays.toString(args));
         
        String ip = "192.168.0.105";
        String client = "root";
        int port = 6080;
        
        if(client.equals("root") && port == 6080){
            new Root(ip);
        }else if(!client.equals("root") && port == 6090){
            new User(ip, client);
        }else{
            System.err.println("Erro na conexão, ip ou porta inválida");
        }
    }
    
}
