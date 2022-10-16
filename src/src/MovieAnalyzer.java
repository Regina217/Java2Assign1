package src;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class MovieAnalyzer {
    List<Movie> movieList;
    public MovieAnalyzer(String dataset_path) {
        try {
            FileInputStream fileInputStream = new FileInputStream(dataset_path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                movieList.add(new Movie(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, Integer> getMovieCountByYear(){
        Map<Integer,Integer> map=movieList.stream().collect(Collectors.groupingBy(Movie::getReleased_Year,Collectors.summingInt(p->1)));
        List<Map.Entry<Integer,Integer>> list=new ArrayList<Map.Entry<Integer, Integer>>(map.entrySet());
        list.stream().sorted(Comparator.comparing(Map.Entry<Integer,Integer>::getKey).reversed()).collect(Collectors.toList());
        LinkedHashMap<Integer,Integer> target=new LinkedHashMap<>();
        for (Map.Entry<Integer,Integer> entry:list) {target.put(entry.getKey(),entry.getValue());}
        return target;
//        TreeMap<Integer,Integer> target=new TreeMap<>(new Comparator<Integer>() {
//            @Override
//            public int compare(Integer o1, Integer o2) {
//                return o2.compareTo(o1);
//            }
//        });
//        target.putAll(map);
        }

    public Map<String, Integer> getMovieCountByGenre(){
        LinkedHashMap<String,Integer> map=movieList.stream().collect(Collectors.groupingBy(Movie::getGenre,LinkedHashMap::new,Collectors.summingInt(p->1)));
        List<Map.Entry<String,Integer>> list=new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        list.stream().sorted(Comparator.comparing(Map.Entry<String,Integer>::getValue).thenComparing(e->e.getKey().charAt(0)).reversed()).collect(Collectors.toList());
        LinkedHashMap<String,Integer> target=new LinkedHashMap<>();
        for (Map.Entry<String,Integer> entry:list) {target.put(entry.getKey(),entry.getValue());}
        return target;
    }

//    public Map<List<String>, Integer> getCoStarCount(){}

    public class Movie{
        String Series_Title ;
        int Released_Year ;
        String Certificate ;
        int Runtime ;//not sure int or string
        String Genre ;
        float IMDB_Rating ;
        String Overview ;
        String Meta_score ;
        String Director ;
        String Star1,Star2,Star3,Star4 ;
        long Noofvotes ;
        long Gross ;

        public Movie(String line) {
            String[] data=line.split(",(?=[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$",-1);
            Series_Title = data[1];
            Released_Year = Integer.parseInt(data[2]);
            Certificate = data[3];
            Runtime = Integer.parseInt(data[4].replace("min",""));
            Genre = data[5];
            this.IMDB_Rating =Float.parseFloat(data[6]) ;
            Overview = data[7];
            Meta_score = data[8];
            Director = data[9];
            Star1 = data[10];
            Star2 = data[11];
            Star3 = data[12];
            Star4 = data[13];
            Noofvotes = Long.parseLong(data[14]);
            Gross = Long.parseLong(data[15]);
        }

        public int getReleased_Year() {
            return Released_Year;
        }
        public String getGenre() {return Genre;}
    }
}