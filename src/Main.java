import java.io.*;
import java.util.*;

public class Main {

    static final String PATH_TO_DESKTOP = "e_shiny_selfies.txt";
    static HashMap<String,Integer> tags;

    static SlideShow slideShow;
    static ArrayList<SubSlideShow> subSlideShows ;
    static ArrayList<SubSlideShow> subSlideShows2 ;
    //static ArrayList<Slide> slides;

    static ArrayList<Image> horizontalImages;
    static ArrayList<Image> vertivalImages;

    static long getCommonTagsTime = 0;
    static long getLeftUniqueTime = 0;
    static long concatenateArray = 0;

    public static void main(String[] args) {

        tags = new HashMap<>();
        horizontalImages = new ArrayList<>();
        vertivalImages = new ArrayList<>();
        subSlideShows = new ArrayList<>();
        subSlideShows2 = new ArrayList<>();
        slideShow = new SlideShow();
        //slides = new ArrayList<>();
        setArray();

//        for(int i=0;i<vertivalImages.size()/2;i++){
//            Slide slide = new Slide(vertivalImages.get(vertivalImages.size()-1),null);
//            subSlideShows.add(new SubSlideShow(slide));
//            vertivalImages.remove(vertivalImages.size()-1);
//        }
       //addVerticals();
        vertivalImages.sort(new Comparator<Image>() {
            @Override
            public int compare(Image o1, Image o2) {
                if(o1.getTagList().size() < o2.getTagList().size()) {
                    return -1;
                } else if(o1.getTagList().size() > o2.getTagList().size()) {
                    return 1;
                }
                return 0;
            }
        });

        while(vertivalImages.size() > 0) {
            System.out.println(vertivalImages.size());
            int j=vertivalImages.size()-1;
            int min = 100000;
            int minIndex = j;
            while(1<j) {
                Slide one = new Slide(vertivalImages.get(0),null);
                Slide two = new Slide(vertivalImages.get(j),null);
                int common = getCommonTags(one.getTags(),two.getTags());
                if(common == 0) {
                    minIndex = j;
                    break;
                }
                if(common < min) {
                    min = common;
                    minIndex = j;
                }
                j--;
            }
            subSlideShows.add(new SubSlideShow(new Slide(vertivalImages.get(0),vertivalImages.get(minIndex))));
            vertivalImages.remove(minIndex);
            vertivalImages.remove(0);
        }


        for(Image image : horizontalImages)
            subSlideShows.add(new SubSlideShow(new Slide(image,null)));


        solve();

       // addCorrectVerticals();

        createOutputFile();
    }

    static void addCorrectVerticals(){
        Random r = new Random();
        for(int i=0;i<slideShow.slides.size();i++) {
            if(slideShow.slides.get(i).first.getOrientation() == Image.ORIENTATION.VERTICAL) {
                System.out.println(i);
                Image leftImage = slideShow.slides.get(i).first;
                int maxScore = -1;
                for(int j=0;j<vertivalImages.size()/10+20;j++) {
                    Image rightImage = vertivalImages.get(r.nextInt(vertivalImages.size()));
                    Slide curSlide = new Slide(leftImage,rightImage);
                    int score = getScoreForPosition(i,curSlide);
                    if(score > maxScore) {
                        maxScore = score;
                        slideShow.slides.set(i,curSlide);
                    }
                }
                vertivalImages.remove(slideShow.slides.get(i).second);
            }
        }
    }

    public static void solve2(){
        Random r = new Random();
        int firstIndex = r.nextInt(subSlideShows.size());
        ArrayList<SubSlideShow> result = new ArrayList<>();
        result.add(subSlideShows.get(firstIndex));
        subSlideShows.remove(firstIndex);

        while(subSlideShows.size() > 0) {
            System.out.println(subSlideShows.size());
            int maxScore = -1;
            int maxIndex = -1;
            for(int i=0;i<subSlideShows.size();i++) {
                SubSlideShow left = result.get(0);
                SubSlideShow right = subSlideShows.get(i);
                int score = getPointsFor2Slides(left.slides.get(left.slides.size() - 1),
                        right.slides.get(0));
                if(score > maxScore) {
                    maxScore = score;
                    maxIndex = i;
                }
            }
            result.get(0).slides.add(result.get(0).slides.size(),subSlideShows.get(maxIndex).slides.get(0));
            subSlideShows.remove(maxIndex);
        }

        slideShow.slides = result.get(0).slides;
    }

    public static void solve(){

        Random random = new Random();
        int initialSize = subSlideShows.size();
        int treshold = 13;
        int treshCounter = 0;
        while (subSlideShows.size() != 1) {

            //print the progress percentage
            System.out.println(100 - ((double)subSlideShows.size()/initialSize) * 100);

            int maxScore = 0;
            SubSlideShow maxResultLeft = null;
            SubSlideShow maxResultRight = null;

            //Start of main program loop.
            //This is the loop that takes most of the running time.
            //Needs to be optimised.
            for (int i = 0; i < subSlideShows.size()*3+200; i++) {
                int randomLeft = random.nextInt(subSlideShows.size());
                int randomRight = random.nextInt(subSlideShows.size());

                SubSlideShow left = subSlideShows.get(randomLeft);
                SubSlideShow right = subSlideShows.get(randomRight);
                if (left.equals(right)) {
                    i--;
                    continue;
                }

                int score = getPointsFor2Slides(left.slides.get(left.slides.size() - 1),
                        right.slides.get(0));

                if (score >= maxScore) {
                    maxScore = score;
                    maxResultLeft = left;
                    maxResultRight = right;
                    if(score >= treshold)
                        break;
                }
            }
            //End of main program loop

            if(maxScore >= treshold) {
                treshCounter = 0;
            } else if(treshCounter > 50) {
                treshold--;
                treshCounter = 0;
            } else {
                treshCounter++;
            }
//
            System.out.println("found "+maxScore +", treshold: "+treshold);
            subSlideShows.add(new SubSlideShow(maxResultLeft, maxResultRight));
            subSlideShows.remove(maxResultLeft);
            subSlideShows.remove(maxResultRight);
        }
        slideShow.slides = subSlideShows.get(0).slides;
        subSlideShows.remove(0);
    }


    public static void improve(){

        printResult();

        ArrayList<Slide> minSlides = new ArrayList<>(slideShow.slides);

        minSlides.sort(new Comparator<Slide>() {
            @Override
            public int compare(Slide o1, Slide o2) {
                int score1 = getScoreForPosition(slideShow.slides.indexOf(o1),o1);
                int score2 = getScoreForPosition(slideShow.slides.indexOf(o2),o2);
                if(score1 < score2){
                    return -1;
                } else if (score1 == score2) {
                    return 0;
                }
                return 1;
            }
        });

        for (int i=0;i<1000;i++) {

            Slide curMin = minSlides.get(i);
            int maxSwapIndex = -1;
            int max = 0;

            for(int j=0; j<slideShow.slides.size();j++) {
                int difference = getDiffrence(slideShow.slides.indexOf(curMin), j);
                if(difference > max) {
                    max = difference;
                    maxSwapIndex = j;
                }
            }

            if(maxSwapIndex > -1) {
                System.out.println(i+ " Improved by " + max);
                Collections.swap(slideShow.slides,slideShow.slides.indexOf(curMin),maxSwapIndex);
            }
        }
    }

    public static int getDiffrence(int one, int two) {
        int startScore = getScoreForPosition(one,slideShow.slides.get(one))+getScoreForPosition(two,slideShow.slides.get(two));
        Collections.swap(slideShow.slides,one,two);

        int endScore = getScoreForPosition(one,slideShow.slides.get(one))+getScoreForPosition(two,slideShow.slides.get(two));
        Collections.swap(slideShow.slides,one,two);

        return endScore-startScore;
    }


    public static int getScoreForPosition(int position, Slide slide) {
        if(position == 0) {
            return getPointsFor2Slides(slide,slideShow.slides.get(1));
        } else if (position == slideShow.slides.size()-1) {
            return getPointsFor2Slides(slideShow.slides.get(position-1),slide);
        } else {
            return getPointsFor2Slides(slide,slideShow.slides.get(position+1)) +
                    getPointsFor2Slides(slideShow.slides.get(position-1),slide);
        }
    }


    public static void addVerticals(){
        Random r = new Random();
        while(vertivalImages.size() > 0) {
            ArrayList<Save> tmp = new ArrayList<>();
            System.out.println("verticals: " + vertivalImages.size());

            for(int i=0;i<300;i++){
                Slide first = new Slide(vertivalImages.get(r.nextInt(vertivalImages.size())),null);
                Slide second = new Slide(vertivalImages.get(r.nextInt(vertivalImages.size())),null);
                if(first.first.getPhotoId() == second.first.getPhotoId()) {
                    i--;
                    continue;
                }
                int common = getCommonTags(first.getTags(),second.getTags());

                tmp.add(new Save(first,second,common));
            }

            tmp.sort(new Comparator<Save>() {
                @Override
                public int compare(Save o1, Save o2) {
                    if(o1.score<o2.score)
                        return -1;
                    else if(o1.score>o2.score)
                        return 1;
                    else
                        return 0;
                }
            });

            int position = tmp.size()/2;
            subSlideShows.add(new SubSlideShow(new Slide(tmp.get(position).firstSlide.first,tmp.get(position).secondSlide.first)));
            vertivalImages.remove(tmp.get(position).firstSlide.first);
            vertivalImages.remove(tmp.get(position).secondSlide.first);
        }
    }


    public static int getPointsFor2Slides(Slide left,Slide right) {
        int common = getCommonTags(left.getTags(),right.getTags());

        return Math.min(Math.min(common,
                left.getTags().size()-common),right.getTags().size()-common);
    }

    //Needs to see if can be further optimised as this is one of the main methods
    public static int getCommonTags(TreeSet<String> leftList,TreeSet<String> rightList) {

        int common = 0;

        Iterator leftIt = leftList.descendingIterator();
        Iterator rightIt = rightList.descendingIterator();

        String leftTag = (String)leftIt.next();
        String rightTag = (String) rightIt.next();
        while(true) {
            if(leftTag.equals(rightTag)) {
                common++;
                if(!leftIt.hasNext() || !rightIt.hasNext())
                    break;
                leftTag = (String)leftIt.next();
                rightTag = (String)rightIt.next();
            } else if (leftTag.compareTo(rightTag) < 0) {
                if(!rightIt.hasNext())
                    break;
                rightTag = (String) rightIt.next();
            } else {
                if(!leftIt.hasNext())
                    break;
                leftTag = (String) leftIt.next();
            }
        }

        return common;
    }


    public static void printResult(){
        int sum = 0;
        ArrayList<SlidePoint> sortedSlides = new ArrayList<>();
        for(int i=0;i<slideShow.slides.size()-1;i++) {
            sum += getPointsFor2Slides(slideShow.slides.get(i), slideShow.slides.get(i + 1));
            sortedSlides.add(new SlidePoint(slideShow.slides.get(i),i,getScoreForPosition(i,slideShow.slides.get(i))));
        }
        System.out.println(sum);
    }


    static class SubSlideShow{
        ArrayList<Slide> slides;
        public SubSlideShow(Slide first){
            slides = new ArrayList<>();
            slides.add(first);
        }
        public SubSlideShow(SubSlideShow left,SubSlideShow right) {
            slides = new ArrayList<>(left.slides);
            slides.addAll(slides.size(),right.slides);
        }
    }

    static class SlidePoint {
        int poistion;
        Slide slide;
        int points;
        SlidePoint(Slide s, int position,int points) {
            this.slide = s;
            this.poistion = position;
            this.points = points;
        }
    }

    public static class Save{
        Slide firstSlide;
        Slide secondSlide;
        int score;
        public Save(Slide first,Slide second,int score){
            this.firstSlide = first;
            this.secondSlide = second;
            this.score = score;
        }
    }

    static class SlideShow {
        ArrayList<Slide> slides = new ArrayList<>();
    }

    static class Slide {
        Image first;
        Image second;
        TreeSet<String> mergedTags = new TreeSet<>();

        public Slide(Image first,Image second) {
            this.first = first;
            if(second != null)
                this.second = second;

            mergeTags();
        }

        public TreeSet<String> getTags(){
            return mergedTags;
        }

        public void mergeTags(){
            for(String tag : first.getTagList())
                mergedTags.add(tag);

            if(second != null) {
                for(String tag : second.getTagList())
                    mergedTags.add(tag);
            }

        }
    }

    //set up the class array from the file given in the file name constant.
    public static void setArray(){
        //Handle the input
        File file = new File(PATH_TO_DESKTOP);
        Scanner scan = null;
        try {scan = new Scanner(file);}
        catch (FileNotFoundException e) {}

        String s = scan.nextLine();
        int numOfimages = Integer.parseInt(s);

        for(int i=0;i<numOfimages;i++) {
            //array[i] = scan.nextLine().split("");
            String[] values = scan.nextLine().split(" ");
            Image image;
            ArrayList<String> list = new ArrayList<>();
            for (int j=2;j<values.length;j++){
                list.add(values[j]);
                if(tags.containsKey(values[j])) {
                    tags.put(values[j],tags.get(values[j])+1);
                } else {
                    tags.put(values[j],1);
                }
            }
            if(values[0].equals("H"))
                image = new Image(Image.ORIENTATION.HORIZONTAL,list,Integer.parseInt(values[1]),i);
            else {
                image = new Image(Image.ORIENTATION.VERTICAL, list, Integer.parseInt(values[1]), i);
            }

            if(values[0].equals("V")){
                vertivalImages.add(image);
            } else {
                horizontalImages.add(image);
            }
        }
    }

    public static void createOutputFile(){
        System.out.println("Printing");
        printResult();
        Writer writer = null;
        File file = new File(System.getProperty("user.home") + "/Desktop/hashcode/a.txt");
        try {
            FileOutputStream is = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(is);
            Writer w = new BufferedWriter(osw);
            w.write(slideShow.slides.size()+"\n");
            for(int i=0; i<slideShow.slides.size();i++){
                if(slideShow.slides.get(i).second != null) {
                    w.write(slideShow.slides.get(i).first.getPhotoId()+" " + slideShow.slides.get(i).second.getPhotoId()+"\n");
                } else {
                    w.write(slideShow.slides.get(i).first.getPhotoId()+"\n");
                }
            }
            w.close();
            osw.close();
            is.close();
        } catch (IOException e) {
            System.err.println("Problem writing to the file statsTest.txt");
        }
    }

}
