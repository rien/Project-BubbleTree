package Experiments;

import java.util.Comparator;

/**
 * @author Rien Maertens
 *         TODO: Description
 */
public class TestResult {

    private String name;
    private int k;
    private long addTime;
    private long containsTime;
    private long removeTime;

    public TestResult(String name,int k, long addTime, long containsTime, long removeTime) {
        this.name = name;
        this.k = k;
        this.addTime = addTime;
        this.containsTime = containsTime;
        this.removeTime = removeTime;
    }

    public long getAddTime() {
        return addTime;
    }

    public int getK() {
        return k;
    }

    public long getContainsTime() {
        return containsTime;
    }

    public long getRemoveTime() {
        return removeTime;
    }

    public String getName() {
        return name;
    }
    
    public long getTotal(){
        return addTime + containsTime + removeTime;
    }
    
    public static Comparator<TestResult> byAddTime(){
        return Comparator.comparing(t->t.addTime);
    }

    public static Comparator<TestResult> byContainsTime(){
        return Comparator.comparing(t->t.containsTime);
    }

    public static Comparator<TestResult> byRemoveTime(){
        return Comparator.comparing(t->t.removeTime);
    }

    public static Comparator<TestResult> byTotalTime(){
        return Comparator.comparing(TestResult::getTotal);
    }

    public static Comparator<TestResult> byName(){
        return Comparator.<TestResult,String>comparing(t->t.name,String.CASE_INSENSITIVE_ORDER);
    }

    @Override
    public String toString() {
        return String.format("%s%nAdd: %d ms%nContains: %d ms%nRemove: %d ms%nTotal: %s ms%n",
                name,addTime,containsTime,removeTime,getTotal());
    }
}
