package util;

import org.bson.Document;
import java.util.List;

public interface OnRetrival {
    public void onRetrival(List<Document> data);
}
