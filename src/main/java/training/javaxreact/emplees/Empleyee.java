package training.javaxreact.emplees;

import java.util.List;
import java.util.stream.IntStream;

public record Empleyee(String name, int yearOfBirth) {
    public int getAge(int year) {
        if(year < yearOfBirth) {
            throw new IllegalArgumentException("Year %d cannot be earlier than year of birth %d".formatted(year, yearOfBirth));
        }
        return year - yearOfBirth;
    }

    public List<Integer> getFirstYearsOfAge() {
        return IntStream.range(1, 6).boxed().map(i -> i + yearOfBirth).toList();
    }
}
