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

    public Map<Integer, Integer> getMovieCountByYear() {
        Map<Integer, Integer> map = movieList.stream().collect(Collectors.groupingBy(Movie::getReleased_Year, Collectors.summingInt(p -> 1)));
        List<Map.Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer, Integer>>(map.entrySet());
        list.stream().sorted(Comparator.comparing(Map.Entry<Integer, Integer>::getKey).reversed()).collect(Collectors.toList());
        LinkedHashMap<Integer, Integer> target = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> entry : list) {
            target.put(entry.getKey(), entry.getValue());
        }
        return target;
//        TreeMap<Integer,Integer> target=new TreeMap<>(new Comparator<Integer>() {
//            @Override
//            public int compare(Integer o1, Integer o2) {
//                return o2.compareTo(o1);
//            }
//        });
//        target.putAll(map);
    }

    public Map<String, Integer> getMovieCountByGenre() {
        LinkedHashMap<String, Integer> map = movieList.stream().collect(Collectors.groupingBy(Movie::getGenre, LinkedHashMap::new, Collectors.summingInt(p -> 1)));
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        list.stream().sorted(Comparator.comparing(Map.Entry<String, Integer>::getValue).reversed().thenComparing(e -> e.getKey().charAt(0))).collect(Collectors.toList());
        LinkedHashMap<String, Integer> target = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            target.put(entry.getKey(), entry.getValue());
        }
        return target;
    }

    public Map<List<String>, Integer> getCoStarCount(){
        List<List<String>> coStarList=new ArrayList<>();
        for (Movie e:movieList) {
            for (List<String> f:e.getCostarList()) {
                coStarList.add(f);
            }
        }
        Map<List<String>,Integer>  target=coStarList.stream().collect(Collectors.groupingBy(e->e,Collectors.summingInt(p->1)));
        return target;
    }
   public List<String> getTopMovies(int top_k, String by){
        List<String> target=new ArrayList<>();
        if (by.equals("runtime")){
            movieList.stream().sorted(Comparator.comparing(Movie::getRuntime).reversed().thenComparing(e -> e.getSeries_Title().charAt(0))).collect(Collectors.toList());
            for (int i = 0; i < top_k; i++) {
                target.add(movieList.get(i).getSeries_Title());
            }
        return target;}
        else if (by.equals("overview")){
            movieList.stream().sorted(Comparator.comparing((Movie e)->{return e.getOverview().length();}).reversed().thenComparing(e -> e.getSeries_Title().charAt(0))).collect(Collectors.toList());
            for (int i = 0; i < top_k; i++) {
                target.add(movieList.get(i).getSeries_Title());
            }
        return target;}
        else return null;
   }

    public class Movie {
        String Series_Title;
        int Released_Year;
        String Certificate;
        int Runtime;//not sure int or string
        String Genre;
        float IMDB_Rating;
        String Overview;
        String Meta_score;
        String Director;
        String Star1, Star2, Star3, Star4;
        long Noofvotes;
        long Gross;

        public Movie(String line) {
            String[] data = line.split(",(?=[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$", -1);
            Series_Title = data[1];
            Released_Year = Integer.parseInt(data[2]);
            Certificate = data[3];
            Runtime = Integer.parseInt(data[4].replace("min", ""));
            Genre = data[5];
            this.IMDB_Rating = Float.parseFloat(data[6]);
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

        public String getGenre() {
            return Genre;
        }

        public String getStar1() {
            return Star1;
        }

        public String getStar2() {
            return Star2;
        }

        public String getStar3() {
            return Star3;
        }

        public String getStar4() {
            return Star4;
        }

        public List<String> getStarList(){
            List<String> target=new ArrayList<>();
            target.add(getStar1());
            target.add(getStar2());
            target.add(getStar3());
            target.add(getStar4());
            return target;
        }

        public List<List<String>> getCostarList() {
            List<String> starList = this.getStarList();
            List<List<String>> target = new ArrayList<>();
            for (int i = 0; i < starList.size(); i++) {
                for (int j = i + 1; j < starList.size(); j++) {
                    List<String> targetPart = new ArrayList<>();
                    if (starList.get(j).charAt(0) - starList.get(i).charAt(0) > 0) {
                        targetPart.add(starList.get(i));
                        targetPart.add(starList.get(j));
                    } else targetPart.add(starList.get(j));
                    targetPart.add(starList.get(i));
                    target.add(targetPart);
                }
            }
            return target;
        }

        public int getRuntime() {
            return Runtime;
        }

        public String getOverview() {
            return Overview;
        }

        public String getSeries_Title() {
            return Series_Title;
        }
    }

//    public static boolean isInTheMovie(String star, Movie movie) {
//        if (movie.getStar1().equals(star)) {
//            return true;
//        } else if (movie.getStar2().equals(star)) {
//            return true;
//        } else if (movie.getStar3().equals(star)) {
//            return true;
//        } else if (movie.getStar4().equals(star)) {
//            return true;
//        } else return false;
//    }

//    public static boolean areInTheMovie(String star1, String star2, Movie movie) {
//        if (isInTheMovie(star1, movie) && isInTheMovie(star2, movie)) {
//            return true;
//        } else return false;
//    }
}