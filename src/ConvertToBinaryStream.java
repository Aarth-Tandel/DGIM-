import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConvertToBinaryStream {

     public Queue<Boolean>[] data;

    @SuppressWarnings("unchecked")
     public ConvertToBinaryStream(){
         data = new Queue[16];
         for(int i=0; i<16; i++) data[i] = new ConcurrentLinkedQueue<>();
     }

     public synchronized void setData(String value){
         if(value.length() > 16){
             System.out.println("Incorrect input data");
             System.exit(-1);
         }
         int integerData = Integer.valueOf(value.trim());
         String binaryValue = Integer.toBinaryString(integerData);
         while(binaryValue.length()<16) binaryValue = "0" + binaryValue;
         for(int i=0; i<16; i++){
             if(binaryValue.charAt(i) == '0')
                 data[i].offer(Boolean.FALSE);
             else
                 data[i].offer(Boolean.TRUE);
         }
     }

     public synchronized Queue<Boolean> getData(int bit){
         return data[bit];
     }
}
