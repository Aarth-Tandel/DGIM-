public class Bucket {
    int size;
    int position;
    int minBucketSize;

    public Bucket(int size, int position){
        this.size = size;
        this.position = position;
    }

    public void setPostion(int position){
        this.position = position;
    }

    public int getPostion(){
        return position;
    }

    public void setSize(int size){
        this.size = size;
    }

    public int getSize(){
        return size;
    }

    public void setMinSize(int min){ this.minBucketSize = min;}

    public int getMinSize(){return this.minBucketSize;}
}
