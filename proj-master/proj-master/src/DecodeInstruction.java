import jdk.swing.interop.SwingInterOpUtils;

import java.util.Hashtable;

public class DecodeInstruction {



    public  static Hashtable<String,String> decode(String Instruction) {
        //System.out.println(Instruction);
        System.out.println("Instruction " + Instruction);

        Hashtable<String, String> h = new Hashtable<>();
        int pcc = Integer.parseInt(controller.PC.content, 2);
        pcc++;
        controller.PC.content = controller.inttoString(pcc, 16);
        if (identifyType(Integer.parseInt(Instruction.substring(0, 4), 2))) {
            h.put("Type", "R");
            h.put("RD", "R" + Integer.parseInt(Instruction.substring(4, 10), 2));
            h.put("R1", controller.registerFile.registers.get("R" + Integer.parseInt(Instruction.substring(4, 10), 2)));//access the register content
            h.put("R2", controller.registerFile.registers.get("R" + Integer.parseInt(Instruction.substring(10, 16), 2)));
        }
    else{
           h.put("Type","I");
           h.put("RD","R"+Integer.parseInt(Instruction.substring(4,10),2));
           h.put("R1",controller.registerFile.registers.get("R"+Integer.parseInt(Instruction.substring(4,10),2)));//access the register content
           h.put("IMM", controller.extend(Instruction.substring(10,16),6));
       }
       h.put("Operation",identifyOP(Integer.parseInt(Instruction.substring(0,4),2)));

       h.put("R1Name","R"+Integer.parseInt(Instruction.substring(4, 10),2));
       h.put("R2Name","R"+Integer.parseInt(Instruction.substring(10, 16),2));

        return h;
    }
    public static boolean identifyType( int s){
        if ( s==0 | s==1 || s==2 || s==5||s==6||s==7)
            return true;
        else
            return false;


    }
    public static String identifyOP (int s){
        switch (s){
            case(0):return"ADD";
            case(1):return"SUB";
            case(2):return"MUL";
            case(3):return"LDI";
            case(4):return "BEQZ";
            case(5):return"AND";
            case(6):return "OR";
            case(7):return"JR";
            case(8):return "SLC";
            case(9):return "SRC";
            case(10):return"LB";
            case(11):return "SB";
            default:return null;

        }

    }
}
