package coding.linkedin;

public class FindCelebrity {

    private static boolean knows(int a, int b) {
        return false;
    }

    public int findCelebrity(int n) {
        int celebrityCandidate = 0;
        for (int i = 0; i < n; i++) {
            if (knows(celebrityCandidate, i)) {
                celebrityCandidate = i;
            }
        }
        if (isCelebrityCandidate(celebrityCandidate, n)) {
            return celebrityCandidate;
        }
        return -1;
    }

    private boolean isCelebrityCandidate(int candidate, int noOfPeople) {
        for (int i = 0; i < noOfPeople; i++) {
            if (i == candidate) {
                continue;
            }

            // If candidate knows i OR i doesn't know candidate, then candidate is not a celebrity
            if (knows(candidate, i) || !knows(i, candidate)) {
                return false;
            }
        }
        // If we've checked all people and haven't returned false, candidate is a celebrity
        return true;
    }
}
