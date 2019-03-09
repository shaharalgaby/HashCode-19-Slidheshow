import java.lang.reflect.Array;
import java.util.ArrayList;

public class Image {
    public enum ORIENTATION {
        HORIZONTAL,
        VERTICAL
    }

    private ORIENTATION orientation;
    private ArrayList<String> tagList;
    private int numberOfTags;
    private int photoId;

    public Image(ORIENTATION orientation, ArrayList<String> tagList, int numberOfTags, int photoId) {
        ArrayList<String> tagsToLower = new ArrayList<>();
        for (String tag : tagList) {
            tagsToLower.add(tag);
        }

        this.orientation = orientation;
        this.tagList = tagsToLower;
        this.numberOfTags = numberOfTags;
        this.photoId = photoId;
    }

    public ORIENTATION getOrientation() {
        return orientation;
    }

    public void setOrientation(ORIENTATION orientation) {
        this.orientation = orientation;
    }

    public ArrayList<String> getTagList() {
        return tagList;
    }

    public void setTagList(ArrayList<String> tagList) {
        this.tagList = tagList;
    }

    public int getNumberOfTags() {
        return numberOfTags;
    }

    public void setNumberOfTags(int numberOfTags) {
        this.numberOfTags = numberOfTags;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }
}