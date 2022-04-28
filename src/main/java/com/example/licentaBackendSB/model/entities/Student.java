package com.example.licentaBackendSB.model.entities;

import com.example.licentaBackendSB.enums.Gender;
import com.example.licentaBackendSB.enums.Master;
import com.example.licentaBackendSB.others.sort.sortingAlgorithms.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity //for hibernate framework
@Table  //for database
@Getter
@Setter// for getters/setters
@AllArgsConstructor // for constructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true) // for building an instance of Student
@ToString
public class Student extends BaseEntity {

    //De aici modifici limitele referitoare la numarul de studenti
    @Transient
    public static final long startIndexing = 1;
    @Transient
    public static final long endIndexing = 10;

    private String nume;
    private String prenume;
    private String grupa;
    private String serie;
    private Integer an;
    @Enumerated(EnumType.STRING)
    private Master master;
    private Boolean isMasterand;
    private Double medie;
    private String zi_de_nastere;
    private String cnp;
    private String judet;
    @Enumerated(EnumType.STRING)
    private Gender genSexual;
    private String myToken;
    private String friendToken;
    private String camin_preferat;
    @OneToOne
    private Camin camin;
    private Boolean flagCazSpecial = Boolean.FALSE;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> friendTokens = new ArrayList<>();

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> numarLocuriCamera = new ArrayList<>();

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

    //Methods for Students ---------------------------------------------------------------------------------------------

    public static void sortStudents(List<Student> tmp)     //todo : modifica comparatorul pt mai multe reguli
    {
        tmp.sort((o1, o2) -> o2.getMedie().compareTo(o1.getMedie()));
    }

    public static void printStudents(List<Student> tmp) {
        System.out.println(Arrays.toString(tmp.toArray()));
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

