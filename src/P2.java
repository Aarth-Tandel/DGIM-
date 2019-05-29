import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class P2 {

    private static String hostName = null;
    private static int portNum = 0;
    private static Object lock1 = new Object();
    private static Object lock2 = new Object();
    private static int K = 0;
    private static int LastQuery = 0;
    private static long initiate = 1;
    private static long currentPostion = 1;
    private static Socket socket;
    private static String R;


    public static void main(String[] args) {
        DGIM[] dgims = new DGIM[16];
        ConvertToBinaryStream dataStream = new ConvertToBinaryStream();

        Thread queryInput = new Thread(() -> {
            try{
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                String line ="";
                String checkStrig = "What is the sum for last";
                int count = 0;
                R = input.readLine();
                if(!R.contains("%")){
                    System.out.println("Illegal R value");
                    System.exit(-1);
                }
                while ((line = input.readLine()) != null) {
                    if (count == 0) {
                        if (!line.contains(":")) {
                            System.out.println("Illegal input port data");
                            System.exit(-1);
                        } else {
                            String[] socketDetails = line.trim().split(":");
                            String port = socketDetails[1];
                            for (int i = 0; i < port.length(); i++) {
                                if (!Character.isDigit((port.charAt(i)))) {
                                    System.out.println("Illegal port data");
                                    System.exit(-1);
                                }
                            }
                            hostName = socketDetails[0];
                            portNum = Integer.valueOf(port);
                            System.out.println(hostName+ " " +portNum);
                            count++;
                        }
                    }else {

                        if (line.equals("end")) {
                            System.out.println("Encountered End of commands. Closing the connection with server");
                            System.out.println("Exiting....");
                            socket.close();
                            System.exit(0);
                        }

                        if(!line.startsWith(checkStrig) && !line.equals("integers") && !line.equals("end")){
                            System.out.println("Illegal input format data. Please try again");
                            System.exit(0);
                        }else {
                            if (!line.equals("end")){
                                String[] query = line.substring((checkStrig.length())).trim().split(" ");
                                if (query != null) {
                                    K = Integer.valueOf(query[0]);
                                    initiate = currentPostion;
                                    System.out.println(line);
                                    if (currentPostion < K) {
                                        Thread.sleep(10);
                                    }

                                    synchronized (lock2) {
                                        lock2.wait();
                                    }
                                }
                            }
                        }
                    }
                }
                LastQuery = K;
                input.close();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        queryInput.start();

        Thread TcpClient = new Thread(() -> {
            try{

                InetAddress host = InetAddress.getByName(hostName);
                socket = new Socket(host.getHostAddress(), portNum);
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                while ((line = input.readLine()) != null) {
                    dataStream.setData(line);
                    if (currentPostion >= K) {
                        synchronized (lock1) {
                            lock1.notify();
                        }
                    }
                    if (currentPostion >= K) {
                        synchronized (lock2) {
                            lock2.wait(1);
                        }
                        initiate = 1;
                    }
                    System.out.println(line);
                    currentPostion++;
                }
                if(socket.getInputStream().read() == -1){
                    System.out.println("End of Stream. Closing the connection with remote server");
                    socket.close();
                    System.exit(0);
                }

                //}
            }catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        });
        TcpClient.start();

        Thread DGIM = new Thread(() -> {
            try{

                for(int i=0; i<16;i++){
                    dgims[i] = new DGIM(dataStream.getData(i),  R);
                }
                synchronized ((lock1)){
                    while(true){
                        lock1.wait();
                        for(int i=0;i<16;i++){
                            dgims[i].calculate();
                        }
                        long  sum = 0;
                        for (int i = 15; i >=0; i--) {
                            sum += dgims[i].getBucketCount(Math.toIntExact(K))* Math.pow(2, 15-i);
                        }

                        //System.out.println("CurrentPosition " + currentPostion+ " K " + K);
                        if((K > 0) && (currentPostion>=K)){
                            System.out.println("The sum of last " + K + " integers is " + sum);
                            K = 0;
                        }

                        if(LastQuery == K ) {
                            //System.out.println(" k " + K + "Last" + LastQuery);
                        }

                        synchronized (lock2) {
                            lock2.notifyAll();
                        }
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        DGIM.start();

    }
}
