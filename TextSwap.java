import java.io.*;
import java.util.*;
import java.lang.Thread;

//Burak Yesil
//I pledge my honor that I have abided by the stevens honor system.

public class TextSwap {

    private static String readFile(String filename, int chunkSize) throws Exception {
        String line;
        StringBuilder buffer = new StringBuilder();
        File file = new File(filename);
	// The "-1" below is because of this:
	// https://stackoverflow.com/questions/729692/why-should-text-files-end-with-a-newline
	if ((file.length()-1) % chunkSize!=0)
	    { throw new Exception("File size not multiple of chunk size"); };
        BufferedReader br = new BufferedReader(new FileReader(file));
        while ((line = br.readLine()) != null){
            buffer.append(line);
        }
        br.close();
        return buffer.toString();
    }

    private static Interval[] getIntervals(int numChunks, int chunkSize) {
        // TODO: Implement me!
        Interval [] intervals = new Interval[numChunks]; //created array of intervals
        int lastStart = 0;
        for (int index = 0; index< numChunks; index++){
            Interval x = new Interval(lastStart, lastStart+chunkSize);
            intervals[index]=x;
           lastStart = lastStart+chunkSize;
        }
        //code to check elements 
        // System.out.println("Printing elements in list");
        // for(int i = 0; i < intervals.length; i++){
        //     System.out.println(intervals[i]);
        // }
        return intervals;
    }

    private static List<Character> getLabels(int numChunks) {
        Scanner scanner = new Scanner(System.in);
        List<Character> labels = new ArrayList<Character>();
        int endChar = numChunks == 0 ? 'a' : 'a' + numChunks - 1;
        System.out.printf("Input %d character(s) (\'%c\' - \'%c\') for the pattern.\n", numChunks, 'a', endChar);
        for (int i = 0; i < numChunks; i++) {
            labels.add(scanner.next().charAt(0));
        }
        scanner.close();
        // System.out.println(labels);
        return labels;
    }



    private static char[] runSwapper(String content, int chunkSize, int numChunks) {
        List<Character> labels = getLabels(numChunks);
        Interval[] intervals = getIntervals(numChunks, chunkSize);

        char[] outputBuff = new char[chunkSize*numChunks];
        for(int i = 0; i < numChunks; i++){
            int offset = (labels.get(i) - 'a') * chunkSize;
            Thread t = new Thread(new Swapper(intervals[i], content, outputBuff, offset ));
            t.start();
            try{
            t.join();
            } catch (InterruptedException x){
                System.out.println("error with joining");
            }
        }
        return outputBuff;
    }

    private static void writeToFile(String contents, int chunkSize, int numChunks) throws Exception {
        char[] buff = runSwapper(contents, chunkSize, contents.length() / chunkSize);
        PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
        writer.print(buff);
        writer.close();
    }

     public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java TextSwap <chunk size> <filename>");
            return;
        }
        String contents = "";
        int chunkSize = Integer.parseInt(args[0]);
        try {
            contents = readFile(args[1],chunkSize);
            writeToFile(contents, chunkSize, contents.length() / chunkSize);
        } catch (Exception e) {
            System.out.println("Error with IO.");
            return;
        }

       //System.out.println(TextSwap.getIntervals(4,4));

    }
}
