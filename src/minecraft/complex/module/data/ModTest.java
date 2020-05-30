package complex.module.data;

import org.apache.commons.lang3.RandomStringUtils;

public class ModTest {
    public static void main(String[] args) {
        System.out.println(RandomStringUtils.randomNumeric(6));
    }

    private static int getRandomInRange(int min, int max){
        return (int) (min + (Math.random()*(Math.abs(min)+Math.abs(max)+1)));
    }
}
