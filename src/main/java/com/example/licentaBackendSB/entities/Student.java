package com.example.licentaBackendSB.entities;

import com.example.licentaBackendSB.others.randomizers.CountyManager;
import com.example.licentaBackendSB.others.randomizers.DoBandCNPandGenderRandomizer;
import com.example.licentaBackendSB.others.randomizers.nameRandomizer;
import com.example.licentaBackendSB.others.randomizers.ygsRandomizer;
import com.example.licentaBackendSB.others.sort.sortingAlgorithms.*;
import lombok.*;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity //for hibernate framework
@Table  //for database
@Getter
@Setter// for getters/setters
@AllArgsConstructor // for constructor
@NoArgsConstructor
@Builder // for building an instance of Student
@ToString
public class Student {

    @Transient
    public static final List<Student> hardcodedStudentsList = hardcodeStudents();

    //De aici modifici limitele referitoare la numarul de studenti
    @Transient
    public static final long startIndexing = 1;
    @Transient
    public static final long endIndexing = 10;

    @Id
    @SequenceGenerator(
            name = "student_sequence",
            sequenceName = "student_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "student_sequence"
    )
    //needed for Statistics && Main sources

    //Fields -----------------------------------------------------------------------------------------------------------
    private Long id;
    private String nume;
    private String prenume;
    private String grupa;
    private String serie;
    private Integer an;
    private Double medie;
    private String zi_de_nastere;
    private String cnp;
    private String judet;
    private String genSexual;
    private String myToken;
    private String friendToken;
    private String camin_preferat;
    private String flagCazSpecial;
    private Integer anUniversitar;

    public static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap, final boolean order) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());

        // Sorting the list based on values
        list.sort((o1, o2) -> order ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()) == 0
                ? o2.getKey().compareTo(o1.getKey())
                : o2.getValue().compareTo(o1.getValue()));
        return list.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));
    }

    //Methods for Statistics -------------------------------------------------------------------------------------------

    public static Map<String, Integer> fillInSortAlgorithmStatistics() {
        Map<String, Integer> performanceStatiticsOfSortAlgorithms = new HashMap<>();

        performanceStatiticsOfSortAlgorithms.put("Collection.sort", 0);
        performanceStatiticsOfSortAlgorithms.put("List.sort", 0);
        performanceStatiticsOfSortAlgorithms.put("QuickSort", 0);
        performanceStatiticsOfSortAlgorithms.put("HeapSort", 0);
        performanceStatiticsOfSortAlgorithms.put("MergeSort", 0);
        performanceStatiticsOfSortAlgorithms.put("SelectionSort", 0);
        performanceStatiticsOfSortAlgorithms.put("InsertionSort", 0);
        performanceStatiticsOfSortAlgorithms.put("BubbleSort", 0);

        return performanceStatiticsOfSortAlgorithms;
    }

    public static List<Student> hardcodeStudents() {
        List<Student> hardcodedListOfStudents = new ArrayList<>();
        Random rand = new Random();

        //manual harcode to test search query => check StudentRepository
        hardcodedListOfStudents.add(
                Student.builder()
                        .id(1L)
                        .nume("Cernat")
                        .prenume("Adrian")
                        .grupa("442")
                        .serie("B")
                        .an(4)
                        .medie(10D)
                        .myToken(shuffleString("cernat" + "adrian"))
                        .zi_de_nastere("23.Ianuarie.1999")
                        .cnp("1990123410036")
                        .genSexual("Masculin")
                        .judet("Bucuresti")
                        .friendToken("null")
                        .camin_preferat("null")
                        .flagCazSpecial("Nu")
                        .anUniversitar(2021)
                        .build());

        for (long i = 1; i < 10; i++) {
            String group = ygsRandomizer.getRandomGroup();
            String series = ygsRandomizer.getRandomSeries();

            int year = Character.getNumericValue(group.charAt(1));

            String randomNume = nameRandomizer.getAlphaNumericString(5);
            String randomPrenume = nameRandomizer.getAlphaNumericString(5);

            String randomDoB = DoBandCNPandGenderRandomizer.getDoB();
            String randomGender = DoBandCNPandGenderRandomizer.getGender();
            String randomCNP = DoBandCNPandGenderRandomizer.getCNP(randomDoB, randomGender);

            String countyCode = randomCNP.substring(7, 9);
            String randomCounty = CountyManager.getCountyFromTwoDigitCode(countyCode);

            hardcodedListOfStudents.add(Student.builder()
                    .id(i + 1)
                    .nume(randomNume)
                    .prenume(randomPrenume)
                    .grupa(group)
                    .serie(series)
                    .an(year)
                    .medie((1D + (10D - 1D) * rand.nextDouble()))
                    .myToken(shuffleString(randomNume + randomPrenume))
                    .zi_de_nastere(randomDoB)
                    .cnp(randomCNP)
                    .genSexual(randomGender)
                    .judet(randomCounty)
                    .friendToken("null")
                    .camin_preferat("null")
                    .flagCazSpecial("Nu")
                    .anUniversitar(2021)
                    .build());
        }
        return hardcodedListOfStudents;
    }

    //Methods for Students ---------------------------------------------------------------------------------------------

    public static void sortStudents(List<Student> tmp)     //todo : modifica comparatorul pt mai multe reguli
    {
        tmp.sort((o1, o2) -> o2.getMedie().compareTo(o1.getMedie()));
    }

    public static void printStudents(List<Student> tmp) {
        System.out.println(Arrays.toString(tmp.toArray()));
    }

    public static String shuffleString(String string) {
        List<String> letters = Arrays.asList(string.split(""));
        Collections.shuffle(letters);
        StringBuilder shuffled = new StringBuilder();
        for (String letter : letters) {
            shuffled.append(letter);
        }
        return shuffled.toString();
    }

    public static long collectionSort(List<Student> tmp) {
        long timeStarted, timeEnded;

        timeStarted = System.nanoTime();
        Collections.sort(tmp, new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                return o2.getMedie().compareTo(o1.getMedie());
            }
        });
        timeEnded = System.nanoTime();
        //System.out.println("Collection.sort: sorting time => " + (timeEnded - timeStarted) + " ns");
        return (timeEnded - timeStarted);
    }

    //Sorting Algorithms returning execution time ----------------------------------------------------------------------

    public static long listSort(List<Student> tmp) {
        long timeStarted, timeEnded;

        timeStarted = System.nanoTime();
        tmp.sort(new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                return o2.getMedie().compareTo(o1.getMedie());
            }
        });
        timeEnded = System.nanoTime();
        //System.out.println("List.sort: sorting time => " + (timeEnded - timeStarted) + " ns");
        return (timeEnded - timeStarted);
    }

    public static long quickSort(List<Student> tmp) {
        long timeStarted, timeEnded;

        timeStarted = System.nanoTime();
        QuickSort.quickSort(tmp, 0, tmp.size() - 1);
        timeEnded = System.nanoTime();
        //System.out.println("QuickSort: sorting time => " + (timeEnded - timeStarted) + " ns");
        return (timeEnded - timeStarted);
    }

    public static long heapSort(List<Student> tmp) {
        long timeStarted, timeEnded;

        timeStarted = System.nanoTime();
        HeapSort.heapSort(tmp);
        timeEnded = System.nanoTime();
        //System.out.println("HeapSort: sorting time => " + (timeEnded - timeStarted) + " ns");
        return (timeEnded - timeStarted);
    }

    public static long mergeSort(List<Student> tmp) {
        long timeStarted, timeEnded;

        timeStarted = System.nanoTime();
        MergeSort.mergeSort(tmp, 0, tmp.size() - 1);
        timeEnded = System.nanoTime();
        //System.out.println("MergeSort: sorting time => " + (timeEnded - timeStarted) + " ns");
        return (timeEnded - timeStarted);
    }

    public static long selectionSort(List<Student> tmp) {
        long timeStarted, timeEnded;

        timeStarted = System.nanoTime();
        SelectionSort.selectionSort(tmp);
        timeEnded = System.nanoTime();
        //System.out.println("SelectionSort: sorting time => " + (timeEnded - timeStarted) + " ns");
        return (timeEnded - timeStarted);
    }

    public static long insertionSort(List<Student> tmp) {
        long timeStarted, timeEnded;

        timeStarted = System.nanoTime();
        InsertionSort.insertionSort(tmp);
        timeEnded = System.nanoTime();
        //System.out.println("InsertionSort: sorting time => " + (timeEnded - timeStarted) + " ns");
        return (timeEnded - timeStarted);
    }

    public static long bubbleSort(List<Student> tmp) {
        long timeStarted, timeEnded;

        timeStarted = System.nanoTime();
        BubbleSort.bubbleSort(tmp);
        timeEnded = System.nanoTime();
        //System.out.println("BubbleSort: sorting time => " + (timeEnded - timeStarted) + " ns");
        return (timeEnded - timeStarted);
    }
}

