package requests.model;

public class Data {
    private final byte[] data;

    public Data(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
