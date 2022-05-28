package com.example.licentaBackendSB.model.entities;

import com.example.licentaBackendSB.enums.Gender;
import com.example.licentaBackendSB.enums.Master;
import com.example.licentaBackendSB.others.sort.sortingAlgorithms.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
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
@SuperBuilder(toBuilder = true)
public class Student extends BaseEntity implements Comparable<Student> {

    //De aici modifici limitele referitoare la numarul de studenti
    @Transient
    public static final long startIndexing = 1;
    @Transient
    public static final long endIndexing = 40;

    private String nume;
    private String prenume;
    private String grupa;
    private String serie;
    private String zi_de_nastere;
    private String cnp;
    private String judet;
    private Boolean isCazSpecial = Boolean.FALSE;
    private Boolean isCazat = Boolean.FALSE;
    private Boolean alreadySelectedUndesiredCamine = Boolean.FALSE;
    private Boolean alreadySelectedPreferences = Boolean.FALSE;
    private Boolean isMasterand;
    private Integer an;
    private Integer prioritate;
    private Double medie;

    @Enumerated(EnumType.STRING)
    private Master master;

    @Enumerated(EnumType.STRING)
    private Gender genSexual;

    @OneToMany(mappedBy = "student", fetch = FetchType.EAGER)
    private Map<Camin, Preferinta> preferinte = new TreeMap<>();

    @ManyToOne
    private Camera cameraRepartizata;

    @ManyToMany
    @JoinTable(name = "student_undesired_accomodation", joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "camin_id", referencedColumnName = "id"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private final List<Camin> mUndesiredAccommodation = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "student_friends", joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id", referencedColumnName = "id"))
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    private List<Student> friends = new ArrayList<>();

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

    @Override
    public String toString() {
        return this.getFullName() + " " + this.cnp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Student student = (Student) o;
        return getId() != null && Objects.equals(getId(), student.getId());
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public int compareTo(Student o) {
        if (!Objects.equals(this.getPrioritate(), o.getPrioritate())) {
            return this.getPrioritate() - o.getPrioritate();
        }
        if (!Objects.equals(this.medie, o.medie)) {
            return -Double.compare(this.getMedie(), o.getMedie());
        }

        return this.getFullName().compareTo(o.getFullName());
    }

    public String getFullName() {
        return this.getNume() + this.getPrenume();
    }
}

