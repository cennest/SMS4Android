package SMS4.Interfaces;

/**
 * Created by salmankhan on 4/16/15.
 */
public interface Notifiable {
//    void notifyEncryptCompletion();
//    void notifyDecryptCompletion();

    public void onSuccess();
    public void onErr(String message);
}
