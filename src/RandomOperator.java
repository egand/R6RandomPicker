import java.io.IOException;
import java.util.Random;

/**
 * Created by Eric on 15/04/2017.
 */
public class RandomOperator {
    private final String[] attacker = {"Recruit","Glaz","Blitz","Buck","Blackbeard","Capitao","Hibana",
            "Jackal","Ash","Fuze","IQ","Sledge","Twitch","Thatcher","Thermite","Montagne"};
    private final String[] defender = {"Recruit","Smoke","Castle","Doc","Mute","Rook","Frost","Valkyrie","Caveira",
            "Echo","Mira","Pulse","Kapkan","Jager","Tachanka","Bandit"};
    private final String[] recruit = {"S.A.S.","FBI Swat","GIGN","Spetsnaz","GSG 9"};
    private int randNum;

    private String getAttacker(int num) {
        return attacker[num];
    }
    private String getDefender(int num) {
        return defender[num];
    }
    private String getRecruit(int num) {
        return recruit[num];
    }


    public String[] generateOperator(char side, String opExc) throws Exception,IOException {
        if (opExc.equals("")) {
            return pickOperator(side, new Random());
        }
        else {
            int[] excNumber = handleOpExceptions(opExc);
            if(excNumber[0] == -1) {
                throw new Exception();
            }
            return pickOperatorWithExc(side, new Random(), excNumber);
        }
    }

    private String[] pickOperator(char side,Random rand) {
        randNum = rand.nextInt(16);
        return sideOperatorPick(side,randNum,rand);

    }

    private int[] handleOpExceptions(String exc) throws IOException {
        String[] excPart = exc.split(",");
        if(excPart.length == 16) throw new IOException();
        int[] excNumber = new int[excPart.length];
        try {
            for(int i=0; i < excPart.length; i++) {
                excNumber[i] = Integer.parseInt(excPart[i]);
            }
        } catch(NumberFormatException e) {
            excNumber[0] = -1;
        }
        return excNumber;
    }

    private String[] pickOperatorWithExc(char side, Random rand, int[] excNum) {
        String[] pick = new String[2];
        randNum = rand.nextInt(16);
        int i = 0;
        while(i < excNum.length) {
            if (randNum == excNum[i]) {
                i = 0;
                randNum = rand.nextInt(16);
            }
            else i++;
        }
        return sideOperatorPick(side,randNum,rand);
    }

    private String[] sideOperatorPick(char side, int randNum, Random rand) {
        String[] pick = new String[2];
        pick[0] = Integer.toString(randNum);
        if (randNum == 0) { //handle recruit
            randNum = rand.nextInt(5);
            pick[1] = "Recruit " + getRecruit(randNum);
        }
        else if(side == 'a') { // attacker selected
            pick[1] = getAttacker(randNum);
        }
        else pick[1] = getDefender(randNum); // defender selected
        return pick;
    }


}
