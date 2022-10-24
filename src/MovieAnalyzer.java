import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class MovieAnalyzer {
    List<Movie> movieList = new ArrayList<>();

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
        Map<Integer, Integer> map = movieList.stream().filter((Movie e)-> !String.valueOf(e.getReleased_Year()).equals("null"))
                .collect(Collectors.groupingBy(Movie::getReleased_Year, Collectors.summingInt(p -> 1)));
        List<Map.Entry<Integer, Integer>> list = new ArrayList<>(map.entrySet());
        List<Map.Entry<Integer, Integer>> targetList = list.stream().sorted(Comparator.comparing(Map.Entry<Integer, Integer>::getKey).reversed()).collect(Collectors.toList());
        LinkedHashMap<Integer, Integer> target = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> entry : targetList) {
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
        List<String> genreList=new ArrayList<>();
        for (Movie m:movieList) {
            for (String s:m.getGenre()) {
                if (!s.equals("")){
                    genreList.add(s);
                }
            }
        }
        LinkedHashMap<String, Integer> map = genreList.stream().collect(Collectors.groupingBy(e->e, LinkedHashMap::new, Collectors.summingInt(p -> 1)));
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        List<Map.Entry<String, Integer>>targetList=list.stream().sorted(Comparator.comparing(Map.Entry<String, Integer>::getValue).reversed().thenComparing(e -> e.getKey().charAt(0))).collect(Collectors.toList());
        LinkedHashMap<String, Integer> target = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : targetList) {
            target.put(entry.getKey(), entry.getValue());
        }
        return target;
    }

    public Map<List<String>, Integer> getCoStarCount() {
        List<List<String>> coStarList = new ArrayList<>();
        for (Movie e : movieList) {
            for (List<String> f : e.getCostarList()) {
                coStarList.add(f);
            }
        }
        Map<List<String>, Integer> target = coStarList.stream().collect(Collectors.groupingBy(e -> e, Collectors.summingInt(p -> 1)));
        return target;
    }

    public List<String> getTopMovies(int top_k, String by) {
        List<String> target = new ArrayList<>();
        if (by.equals("runtime")) {
            List<Movie> temp = movieList.stream().filter((Movie e) -> { return e.getRuntime()!=0; } )
                    .sorted(Comparator.comparing(Movie::getRuntime).reversed()
                            .thenComparing(new Comparator<Movie>() {
                                @Override
                                public int compare(Movie o1, Movie o2) {
                                    int l=Math.min(o1.getSeries_Title().length(),o2.getSeries_Title().length());
                                    for (int i = 0; i < l; i++) {
                                        if(o1.getSeries_Title().charAt(i)-o2.getSeries_Title().charAt(i)!=0)  return o1.getSeries_Title().charAt(i)-o2.getSeries_Title().charAt(i);
                                    }
                                    return 0;
                                }
                            }))
                    .collect(Collectors.toList());
            for (int i = 0; i < top_k; i++) {
                target.add(temp.get(i).getSeries_Title());
            }
            return target;
        } else if (by.equals("overview")) {
            List<Movie> temp=movieList.stream().filter((Movie e)-> {return !e.getOverview().equals("");})
                    .sorted(Comparator.comparing((Movie e) -> {return e.getOverview().length();})
                            .reversed().thenComparing(new Comparator<Movie>() {
                                @Override
                                public int compare(Movie o1, Movie o2) {
                                    int l=Math.min(o1.getSeries_Title().length(),o2.getSeries_Title().length());
                                    for (int i = 0; i < l; i++) {
                                        if(o1.getSeries_Title().charAt(i)-o2.getSeries_Title().charAt(i)!=0)  return o1.getSeries_Title().charAt(i)-o2.getSeries_Title().charAt(i);
                                    }
                                    return 0;
                                }
                            }))
                    .collect(Collectors.toList());
            for (int i = 0; i < top_k; i++) {
                target.add(temp.get(i).getSeries_Title());
            }
            return target;
        } else return null;
    }

    public List<String> getTopStars(int top_k, String by) {
        List<String> target = new ArrayList<>();
        IdentityHashMap<String, Float> r = new IdentityHashMap<>();
        IdentityHashMap<String, Long> g = new IdentityHashMap<>();
        for (Movie m : movieList) {
            for (String star : m.getStarList()) {
                if(!star.equals("")&&!String.valueOf(m.getIMDB_Rating()).equals("")){r.put(star, m.getIMDB_Rating());}
                if(!star.equals("")&&m.getGross()!=0){g.put(star, m.getGross());}
            }
        }
        if (by.equals("rating")) {
            List<Map.Entry<String, Double>> temp = new ArrayList<>(r.entrySet().stream().collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.averagingDouble(Map.Entry::getValue))).entrySet());
            List<Map.Entry<String, Double>> targetList=temp.stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed().thenComparing(new Comparator<Map.Entry<String, Double>>() {
                        @Override
                        public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                            int l=Math.min(o1.getKey().length(),o2.getKey().length());
                            for (int i = 0; i < l; i++) {
                                if(o1.getKey().charAt(i)-o2.getKey().charAt(i)!=0)  return o1.getKey().charAt(i)-o2.getKey().charAt(i);
                            }
                            return 0;
                        }
                    }))
                    .collect(Collectors.toList());
            for (int i = 0; i < top_k; i++) {
                target.add(targetList.get(i).getKey());
            }
        }
        if (by.equals("gross")) {
            List<Map.Entry<String, Double>> temp = new ArrayList<>(g.entrySet().stream().collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.averagingDouble(Map.Entry::getValue))).entrySet());
            List<Map.Entry<String, Double>> targetList=temp.stream()
                    .sorted(Comparator.comparing((Map.Entry<String, Double> e) -> e.getValue())
                            .reversed().thenComparing((o1, o2) -> {
                                int l=Math.min(o1.getKey().length(),o2.getKey().length());
                                for (int i = 0; i < l; i++) {
                                    if(o1.getKey().charAt(i)-o2.getKey().charAt(i)!=0)  return o1.getKey().charAt(i)-o2.getKey().charAt(i);
                                }
                                return 0;
                            }))
                            .collect(Collectors.toList());
            for (int i = 0; i < top_k; i++) {
                target.add(targetList.get(i).getKey());
            }
        }
        return target;
    }

    public List<String> searchMovies(String genre, float min_rating, int max_runtime) {
        List<String> target = new ArrayList<>();
        List<Movie> targetList= movieList.stream()
                .filter((Movie e)-> {return !e.getGenre().equals("")&&e.getRuntime()!=0&&e.getIMDB_Rating()!=0;})
                .filter(e -> e.getIMDB_Rating() >= min_rating && e.getRuntime() <= max_runtime && contain(e.getGenre(),genre))
                .sorted(new Comparator<Movie>() {
                    @Override
                    public int compare(Movie o1, Movie o2) {
                        int l=Math.min(o1.getSeries_Title().length(),o2.getSeries_Title().length());
                        for (int i = 0; i < l; i++) {
                            if(o1.getSeries_Title().charAt(i)-o2.getSeries_Title().charAt(i)!=0)  return o1.getSeries_Title().charAt(i)-o2.getSeries_Title().charAt(i);
                        }
                        return o1.getSeries_Title().length()-o2.getSeries_Title().length();
                    }
                })
                .collect(Collectors.toList());
        for (Movie m : targetList) {
            target.add(m.getSeries_Title());
        }
        return target;
    }

    public static boolean contain(ArrayList list,String s){
        String[] a=s.split(",");
        for (String e:a) {
            if(list.contains(e)) return true;
        }
        return false;
    }


    public static boolean nameFirst(String o1,String o2){
        int l=Math.min(o1.length(),o2.length());
        for (int i = 0; i < l; i++) {
            if(o1.charAt(i)-o2.charAt(i)<0)  return true;
            if(o1.charAt(i)-o2.charAt(i)>0)  return false;
        }
        if(o1.length()<o2.length())return true;
        else return false;
    }
    public class Movie {
        String Series_Title;
        int Released_Year;
        String Certificate;
        int Runtime;//not sure int or string
        String[] Genre;
        float IMDB_Rating;
        String Overview;
        String Meta_score;
        String Director;
        String Star1, Star2, Star3, Star4;
        long Noofvotes;
        long Gross;

        public Movie(String line) {
            String[] data = line.trim().split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1);
            if(data[1].startsWith("\"")||data[1].endsWith("\"")){Series_Title = data[1].substring(1,data[1].length()-1).trim();}
            else Series_Title = data[1].trim();;
            Released_Year = Integer.parseInt(data[2]);
            Certificate = data[3];
            String time=data[4].replace(" min", "");
            if(!time.equals("")){Runtime = Integer.parseInt(time);}
            if(data[5].startsWith("\"")||data[5].endsWith("\"")){Genre = data[5].substring(1,data[5].length()-1).trim().split(",");}
            else Genre = data[5].trim().split(",");
            this.IMDB_Rating = Float.parseFloat(data[6]);
            Overview = data[7];
            Meta_score = data[8];
            Director = data[9];
            Star1 = data[10];
            Star2 = data[11];
            Star3 = data[12];
            Star4 = data[13];
            Noofvotes = Long.parseLong(data[14]);
            String gross=data[15].replaceAll("[^0-9]","");
            if(!gross.equals("")){Gross = Long.parseLong(gross);}
        }

        public int getReleased_Year() {
            return Released_Year;
        }

        public ArrayList<String> getGenre() {
            ArrayList<String> target=new ArrayList<>();
            for (String s:Genre) {
                String t=s.trim();
                if(!t.equals("")){
                    target.add(t);
                }
            }
            return target;
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

        public List<String> getStarList() {
            List<String> target = new ArrayList<>();
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
                    if (starList.get(i) != null && starList.get(j) != null){
                        if (nameFirst(starList.get(i),starList.get(j))) {
                            targetPart.add(starList.get(i));
                            targetPart.add(starList.get(j));
                        } else {
                            targetPart.add(starList.get(j));
                            targetPart.add(starList.get(i));}
                    target.add(targetPart);
                }
                }
            }
            return target;
        }

        public int getRuntime() {
            return Runtime;
        }

        public String getOverview() {
            String target;
            if(Overview.startsWith("\"")||Overview.endsWith("\"")){
                target=Overview.substring(1,Overview.length()-1);
            }
            else target=Overview;
            return target;
        }

        public String getSeries_Title() {
            return Series_Title;
        }

        public float getIMDB_Rating() {
            return IMDB_Rating;
        }

        public long getGross() {
            return Gross;
        }
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