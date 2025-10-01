/**
 * Kevin Bacon Game interface
 * PS4
 * @author Godwin Kangor,winter 2024
 * @Ahmed Elmi, winter 2024
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class BaconGame {
    Graph<String, List<String>> movieActors = new AdjacencyMapGraph<>(); //initializing graph containing all vertices
    List<String> sharedMovie; //list containing all actors who acted in a specific movie
    String center; //center of the universe
    Boolean gameOn = true; //boolean to keep track of game progress

    /**
     * reads the contents of a file with the specified filename and loads to a list of strings.
     *
     * @param filename the name of the file to be loaded
     * @return a list of strings containing the contents of the file
     * @throws Exception if there is an error during the reading process
     */
    public List<String> loadFileToString(String filename) throws Exception {
        //reading the file
        BufferedReader in = new BufferedReader(new FileReader(filename));
        List<String> stringlist = (List<String>) new ArrayList<String>();
        String str = "";
        while ((str = in.readLine()) != null) stringlist.add(str);//loading the file to string
        in.close();
        return stringlist;
    }

    /**

     Reads in data from three separate files containing information about actors and movies,
     creates three hashmaps, and uses the data to create a graph representing the relationships
     between the actors and movies. Returns the graph as a Graph object.
     @param movieActors name of the file containing information about actors
     @param movieNames name of the file containing information about movies
     @param actorID name of the file containing information about the relationships
     @return a Graph object representing the relationships between the actors
     @throws Exception if there is an error reading in the data from the input files
     */
    public Graph<String, List<String>> baconReader(String movieActors, String movieNames, String actorID) throws Exception {
        //Creating hashmaps for each file
        HashMap<String, String> movieID_Name = new HashMap<>(); //key = movie_ID :value = name of movie
        HashMap<String, String> actorID_Name = new HashMap<>(); //key =  actor_ID:value = name of actor
        HashMap<String, List<String>> movieID_ActorID = new HashMap<>(); //key = movie ID:value = actor ID

        // file reading - actors details
        List<String> actor_List = loadFileToString(movieActors);
        for (String string : actor_List) {
            String[] splitstring = string.split("\\|");
            actorID_Name.put(splitstring[0], splitstring[1]);
        }
        // file reading - movie details
        List<String> movie_List = loadFileToString(movieNames);
        for (String movie : movie_List) {
            String[] movieSplit = movie.split("\\|");
            movieID_Name.put(movieSplit[0], movieSplit[1]);
            movieID_ActorID.put(movieSplit[0], new ArrayList<String>());
        }

        // file reading - movie ID and actor ID
        List<String> list = loadFileToString(actorID);
        List<String> listActors = new ArrayList<String>();
        for (String actor : list) {
            String[] splitActors = actor.split("\\|");
            listActors = movieID_ActorID.get(splitActors[0]);
            listActors.add(splitActors[1]); // adds actor to list of actors
            movieID_ActorID.put(splitActors[0], listActors); // updates hashmap
        }

        for (String actor : actorID_Name.keySet()) {
            this.movieActors.insertVertex(actorID_Name.get(actor));
        }
        //create a graph in which vertices are names of actors and edges are movie they share
        for (String a : movieID_ActorID.keySet()) {
            String movieName = movieID_Name.get(a);
            List<String> actors = movieID_ActorID.get(a);
            //loop through list of actors
            for (int i = 0; i < actors.size() - 1; i++) {
                for (int j = 0; j < actors.size(); j++) {
                    String actorI = actorID_Name.get(actors.get(i));
                    String actorJ = actorID_Name.get(actors.get(j));
                    //checking if they are different
                    if (!Objects.equals(actorJ, actorI)) {
                        //check if there is no edge between actors
                        if (!this.movieActors.hasEdge(actorJ, actorI)) {
                            //initializing shared movie to be null if no edge exists
                            sharedMovie = new ArrayList<String>();
                        } else {
                            //setting shared movie to be the list of movies if edge exists
                            sharedMovie = this.movieActors.getLabel(actorJ, actorI);
                        }
                        //adding current movie
                        if (!sharedMovie.contains(movieName)) {
                            sharedMovie.add(movieName);
                        }
                        //updating in graph
                        this.movieActors.insertUndirected(actorJ, actorI, sharedMovie);
                    }
                }
            }
        }
        return this.movieActors;
    }

    /**
     * Method to call out different methods when specific keys are pressed
     *
     * @throws Exception
     */

    public void commandKey() throws Exception {
        center = "Kevin Bacon";
        Graph<String, List<String>> graph = GraphLibrary.bfs(movieActors, center); //subgraph

        while (gameOn) {
            //initializing the scanner
            Scanner scanner = new Scanner(System.in);
            System.out.println("What command would you like ?");
            String userInput = scanner.nextLine();

            //assigning the first section of the word as command
            char command = userInput.charAt(0);

            // quits the system
            if (command == 'q') {
                gameOn = false;
            }

            // centers an actor
            if (command == 'u') {
                String name = userInput.substring(2);

                //check if actor is in the map
                if (movieActors.hasVertex(name)) {
                    System.out.println("u " + name);
                    center = name;

                    //find the number of nodes in tree from bfs
                    int size = 0;
                    Graph<String, List<String>> graph1 = GraphLibrary.bfs(movieActors, center);
                    size = graph1.numVertices();
                    double separation = GraphLibrary.averageSeparation(graph1, center);
                    System.out.println(center + " is now the center of the acting universe, connected to " + size + " actor(s) with average separation " + GraphLibrary.averageSeparation(graph1, center));
                } else {
                    System.out.println("Actor not in map, try one who is");
                }
            }

            // find path from an actor to current center of the universe
            if (command == 'p') {
                String name = userInput.substring(2);
                Graph<String, List<String>> subgraph = GraphLibrary.bfs(movieActors, center);
                Set<String> missingVertex = GraphLibrary.missingVertices(movieActors,subgraph);

                if(missingVertex.contains(name)) {
                    System.out.println("There is no path");
                }
                else{
                    if (!movieActors.hasVertex(name)) {
                        System.out.println("Actor not in graph, try one who is");
                    }

                    else {
                        //shortest graph
                        Graph<String, List<String>> graph1 = GraphLibrary.bfs(movieActors, center);
                        //acquiring the shortest path and storing it in a list
                        List<String> path = GraphLibrary.getPath(graph1, name);
                        System.out.println(center + "game >");
                        System.out.println(name + "'s number is " + (path.size() - 1));
                        int count = 0;

                        while (count < path.size() - 1) {
                            List<String> sharedMovie = movieActors.getLabel(path.get(count), path.get(count + 1));
                            System.out.println(path.get(count) + " appeared in " + sharedMovie + " with " + path.get(count + 1));
                            count++;
                        }
                    }
                }
            }

            // list actors sorted by non-infinite separation from the current center, with separation between low and high
            if (command == 's') {
                //assigning low and high variables according to the user input
                int low = Integer.parseInt(userInput.substring(2, 3));
                int high = Integer.parseInt(userInput.substring(4));

                //create an array list containing names of actors
                List<String> actors = new ArrayList<>();
                HashMap<String, Integer> actorSeparation = new HashMap<String, Integer>(); //stores each vertex's separation
                Graph<String, List<String>> subgraph = GraphLibrary.bfs(movieActors, center);
                //looping through vertices in subgraph then adding them to the list of actors and hashmap
                for (String vertex : subgraph.vertices()) {
                    int pathSize = GraphLibrary.getPath(subgraph, vertex).size() - 1; // size of list from get path
                    if (low <= pathSize && pathSize <= high) {
                        actors.add(vertex);
                        actorSeparation.put(vertex, pathSize);
                    }
                }
                //sorting vertices by separation value
                actors.sort((String s1, String s2) -> actorSeparation.get(s1) - actorSeparation.get(s2));
                System.out.println(actors);
            }

            // list actors sorted by degree, with degree between low and high
            if (command == 'd') {
                //assigning low and high variables according to the user input
                int low = Integer.parseInt(userInput.substring(2, 3));
                int high = Integer.parseInt(userInput.substring(4));

                //checking if the user has inputted in the numbers right order
                if (low > high) {
                    System.out.println("Order the numbers well and try again.");
                } else {
                    //list to store vertices within range of low and high
                    List<String> qualifiedVertices = new ArrayList<>();

                    //looping through all vertices in main graph
                    for (String vertices : GraphLibrary.verticesByInDegree(movieActors)) {
                        //checks if vertex separation is within range
                        if (movieActors.inDegree(vertices) <= high && movieActors.inDegree(vertices) >= low) {
                            //adds vertex to the list if qualified
                            qualifiedVertices.add(vertices);
                        }
                    }

                    System.out.println("Command d output: " + qualifiedVertices);
                }
            }

            // list actors with infinite separation from the current center
            if(command == 'i'){
                Graph<String, List<String>> subgraph = GraphLibrary.bfs(movieActors, center);
                Set<String> missingVertices = GraphLibrary.missingVertices(movieActors,subgraph);
                System.out.println(" The following are the missing vertices " + missingVertices);
            }

            // list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation
            if (command == 'c') {
                //acquiring the number inputted by the user
                String string = userInput.substring(2);
                int num = Integer.parseInt(string);
                //array list to keep track of
                List<String> averageList = new ArrayList<String>();
                Graph<String, List<String>> subgraph = GraphLibrary.bfs(movieActors, center);
                Set<String> missing = GraphLibrary.missingVertices(movieActors, subgraph);

                //loop through each vertex to make sure it's connected to the centre

                //compare the average separations
                averageList.sort((String s1, String s2) -> (int) GraphLibrary.averageSeparation(GraphLibrary.bfs(movieActors, s1), s1) - (int) GraphLibrary.averageSeparation(GraphLibrary.bfs(movieActors, s2), s2));
                ArrayList<String> orderedList = new ArrayList<>(); //vertices in sorted list within the range
                if (num > 0) {
                    if (num > averageList.size()) {
                        orderedList.addAll(averageList);
                    } else {
                        for (int count = 0; count < num; count++) {
                            orderedList.add(averageList.get(count));
                        }
                    }
                } else {
                    if (Math.abs(num) > averageList.size()) {
                        orderedList.addAll(averageList);
                    } else {
                        for (int count = averageList.size() - 1; count < Math.abs(num); count--) {
                            orderedList.add(averageList.get(count));
                        }
                    }
                }

                System.out.println(orderedList);
            }
        }
    }
    public static void main(String[] args) throws Exception {
        Graph<String, String> relationships = new AdjacencyMapGraph<>();
        Graph<String, String> pathTree = new AdjacencyMapGraph<>(); //graph from bfs

        relationships.insertVertex("Alice");
        relationships.insertVertex("Bob");
        relationships.insertVertex("Charlie");
        relationships.insertVertex("Dartmouth");
        relationships.insertVertex("Nobody");
        relationships.insertVertex("Nobody's Friend");
        relationships.insertVertex("Kevin Bacon");


        // add edges
        relationships.insertUndirected("Bob", "Charlie", "C movie");
        relationships.insertUndirected("Bob", "Alice", "A movie");
        relationships.insertUndirected("Bob", "Kevin Bacon", "A movie");
        relationships.insertUndirected("Alice", "Kevin Bacon", "A movie");
        relationships.insertUndirected("Alice", "Kevin Bacon", "E movie");
        relationships.insertUndirected("Alice", "Charlie", "D movie");
        relationships.insertUndirected("Charlie", "Dartmouth", "B movie");
        relationships.insertUndirected("Nobody", "Nobody's Friend", "F movie");
//		System.out.println(relationships);

        // test bfs method
        pathTree = GraphLibrary.bfs(relationships, "Alice");
        System.out.println("BFS path tree from Alice: " + pathTree);


        // test getPath method
        List<String> path = GraphLibrary.getPath(pathTree, "Charlie");
        System.out.println("Path from Charlie to Alice: " + path);

        //testing missing vertices method
        Set <String> missingset  = GraphLibrary.missingVertices(relationships, pathTree);
        System.out.println("Vertices in the missing set: " + missingset);


        //Kevin Bacon Game Test
        System.out.println("*****************");
        System.out.println("KEVIN BACON GAME");
        System.out.println("Commands:\n\tc <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation\n\t" +
                "d <low> <high>: list actors sorted by degree, with degree between low and high\n\t" +
                "i: list actors with infinite separation from the current center\n\t" +
                "p <name>: find path from <name> to current center of the universe\n\t" +
                "s <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high\n\t" +
                "u <name>: make <name> the center of the universe\n\t" +
                "q: quit game\n");
//        String actorFile= "PS4/actorsTest.txt";
        String actorFile = "PS4/actors.txt";
        String movieFile = "PS4/movies.txt";
//        String movieFile = "PS4/moviesTest.txt";
        String movieActorFile = "PS4/movie-actors.txt";
//        String movieActorFile = "PS4/movie-actorsTest.txt";
        String center = "Kevin Bacon";
        BaconGame game = new BaconGame();
        Graph<String, List<String>> movieactorgraph = game.baconReader(actorFile, movieFile, movieActorFile);
        Graph<String, List<String>> moviebfsgraph = GraphLibrary.bfs(movieactorgraph,center);

        game.commandKey();
    }
}
