import java.util.Deque;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class DGIM {
    private Queue<Boolean> data;
    private Deque<Bucket> bucketData;
    private int currentPosition;
    private int minBucketSize;


    public DGIM(Queue<Boolean> stream, String R){
        data = stream;
        bucketData = new ConcurrentLinkedDeque<>();
        currentPosition=1;
        minBucketSize = calculateMinBucketSize(R);
    }

    private int calculateMinBucketSize(String R){
        switch (R) {
            case "33%":
                return 3;
            case "50%":
                return 2;
            case "25%":
                return 4;
            case "20%":
                return 5;
            case "16%":
                return 6;
            case "17%":
                return 6;
            default:
                return 2;
        }
    }

    public void calculate(){
        while(!data.isEmpty()){
            if(data.poll()) addBucket(currentPosition++, minBucketSize);
            else currentPosition++;
        }

    }

    public int getBucketCount(int k){
        int result = 0;
        int value = 0;
        if (bucketData.isEmpty()) return 0;
        int distance = currentPosition - bucketData.getFirst().getPostion();
        if (distance > k) {
            return 0;
        } else {
            Iterator<Bucket> iter = bucketData.iterator();
            while (iter.hasNext()) {
                Bucket cur = iter.next();
                if (cur.getPostion() >= (currentPosition - k)) {
                    value = cur.getSize();
                    result += value;
                } else {
                    break;
                }
            }
        }
        result -= value / 2;
        return result;
    }

    private void addBucket(int position, int minBucketSize){
        bucketData.addFirst(new Bucket(1,position));

        Iterator<Bucket> iter = bucketData.iterator();
        if(checkMerge(iter)){
            Iterator<Bucket> temp = bucketData.iterator();
            merge(temp);
        }
    }

    private boolean checkMerge(Iterator<Bucket> iter) {
        Bucket top, second, third, fourth;
        if(iter.hasNext()){
            top = iter.next();
            if(iter.hasNext()){
                second = iter.next();
                if(top.getSize() != second.getSize())return false;
                else{
                    if(iter.hasNext()){
                        third = iter.next();
                        if(second.getSize() != third.getSize()) return false;
                        else{
                            if(iter.hasNext()){
                                fourth = iter.next();
                                if(third.getSize() == fourth.getSize())return true;
                                else return false;
                            }
                            else return false;
                        }
                    }
                    else return false;
                }
            }
            else return false;
        }
        else return false;

    }

    private void merge(Iterator<Bucket> temp) {
        temp.next();temp.next();
        Bucket third = temp.next();
        Bucket fourth = temp.next();
        int previousBucketSize = third.getSize();
        third.setSize(third.getSize() + fourth.getSize());
        third.setPostion(third.getPostion());
        temp.remove();
        Iterator<Bucket> temp2 = getIterator(previousBucketSize);
        if(checkMerge(temp2)){
            Iterator<Bucket> temp3 = getIterator(previousBucketSize);
            merge(temp3);
        }
    }

    private Iterator<Bucket> getIterator(int previousBucketSize) {
        Iterator<Bucket> iter = bucketData.iterator();
        int count = 0;
        while(iter.hasNext()){
            Bucket temp = iter.next();
            if(temp.getSize() == previousBucketSize) count++;
            if(count == 2) return iter;
        }
        return null;
    }
}
