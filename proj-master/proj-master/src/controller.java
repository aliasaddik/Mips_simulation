import jdk.swing.interop.SwingInterOpUtils;

import javax.print.DocFlavor;
import javax.print.attribute.IntegerSyntax;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Set;

public class controller {
    public static InstructionMemory Instruction= new InstructionMemory();
    public static dataMemory data= new dataMemory();;
    public static FetchInstruction Fetch;
    public static PC PC = new PC("PC");;
    public static SREG SREG= new SREG("SREG");;
    public static registerFile registerFile= new registerFile();

    public controller() {

        Instruction = new InstructionMemory();
        data = new dataMemory();
        //Fetch = new FetchInstruction(Instruction);
        PC = new PC("PC");
        SREG = new SREG("SREG");
        registerFile= new registerFile();

        }

        public static  String inttoString ( int x, int nob ){
            String temp;
            if (x>=0) {
                temp = String.format("%" + nob + "s", Integer.toBinaryString(x)).replace(' ', '0');
                return temp.substring(temp.length() - nob , temp.length());
            }
        else
            return  Integer.toBinaryString(x).substring(32-nob,32) ;

        }

        public static int binToInt (String x , int nob){

        if (x.charAt(0)=='1'){
            int z= Integer.parseInt(x,2)  ;
            z= ~z;
            if (nob ==8)
            z= z& 0b00000000000000000000000011111111;
            else if (nob ==16){
                z=z& 0b00000000000000001111111111111111;
            }
            else if(nob==6){
                z= z& 0b00000000000000000000000000111111;
            }
            z= z+1;
             return -z;
        }
       else{
           return  Integer.parseInt(x,2)  ;}
        }
/////////////////////////////////
        public static String extend (String x ,int nob ){
        if (x.charAt(0)=='1'){
            return String.format("%"+nob+"s",x).replace(' ', '1');
        }
        else{
            return String.format("%"+nob+"s", x).replace(' ', '0');
        }}

       public  void setRegister(String r, String val){
        this.registerFile.registers.replace(r,val);
    }
    public static String fetch(String PC){
        return  controller.Instruction.memory[Integer.parseInt(PC,2)];
    }
    public static boolean identifyType( int s){

        if ( s==0 | s==1 || s==2 || s==5||s==6||s==7)
            return true;
        else
            return false;


    }
    public static void readFile(String path) throws FileNotFoundException , IOException {

        BufferedReader reader = new BufferedReader(new FileReader(path));
        String row="";
        String output="";
        String [] input;
        while ((row = reader.readLine()) != null) {
            input=row.split(" ");
            parseInstruction(input);
        }
    }

    public  static void parseInstruction(String [] input){
        String result="";
        switch(input[0]) {
            case ("ADD"):
                result+="0000";
                break;
            case ("SUB"):
                result+="0001";
                break;
            case ("MUL"):
                result+="0010";
                break;
            case ("LDI"):
                result+="0011";
                break;
            case ("BEQZ"):
                result+="0100";
                break;
            case ("AND"):
                result+="0101";
                break;
            case ("OR"):
                result+="0110";
                break;
            case ("JR"):
                result+="0111";
                break;
            case ("SLC"):
                result+="1000";
                break;
            case ("SRC"):
                result+="1001";
                break;
            case ("LB"):
                result+="1010";
                break;
            case ("SB"):
                result+="1011";
                break;
        }
        String register="";
        String binary="";
        int number;

        if(identifyType(Integer.parseInt(result,2))){

            register = input[1].substring(1);
            number=Integer.parseInt(register);
            binary=inttoString(number,6);
            result+=binary;

            register = input[2].substring(1);
            number=Integer.parseInt(register);
            binary=inttoString(number,6);
            result+=binary;
        }
        else {

            register = input[1].substring(1);
            number=Integer.parseInt(register);
            binary=inttoString(number,6);
            result+=binary;

            number=Integer.parseInt(input[2]);
            binary=inttoString(number,6);
            result+=binary;

        }

        Instruction.memory[Instruction.pointer]=result;
        Instruction.pointer++;


    }
//(Instruction.pointer) >= Integer.parseInt(PC.content,2)

    public static void runProgram(){
        int clockCycles=  3 + ((Instruction.pointer - 1) * 1);

        String fetched="";
        Hashtable<String,String> decoded=null;
        int i=1;
        boolean jumpFlag=false;
        int jumpCounter=0;

        while( i<=clockCycles ){
            System.out.println("Clock Cycle Number: "+i );
            System.out.println("-----------------------------");

            if(Instruction.memory[Integer.parseInt(PC.content,2)]==null ){
                ExecuteInstruction.Execute(decoded);
                break;
            }
            else if(i==1) {
                fetched = fetch(PC.content);
                i++;
            }
            else if (i==2) {
                decoded = DecodeInstruction.decode(fetched);
                fetched = fetch(PC.content);
                i++;
            }
            else if(i==clockCycles){
                ExecuteInstruction.Execute(decoded);
                break;
            }
            else if(i==clockCycles-1) {
                ExecuteInstruction.Execute(decoded);
                decoded = DecodeInstruction.decode(fetched);
                i++;
            }
            else{
                if(jumpCounter==1){
                    fetched= fetch(PC.content);
                    jumpCounter++;

                }
                else if(jumpCounter==2){
                    decoded = DecodeInstruction.decode(fetched);
                    fetched = fetch(PC.content);
                    jumpCounter=0;
                }
                else {
                    jumpFlag=false;
                    jumpFlag = ExecuteInstruction.Execute(decoded);
                    printExecuted(decoded);
                    if (jumpFlag != true) {
                        decoded = DecodeInstruction.decode(fetched);
                        fetched = fetch(PC.content);

                    } else {
                        jumpCounter = 1;
                    }
                }
                i++;

            }


        }

    }
    public static void printExecuted(Hashtable<String, String> input) {

        String instruction ="";
        instruction = instruction + input.get("Operation");
        if(input.get("Type").equals("R")){
            instruction= instruction+" "+ input.get("R1Name");
            instruction= instruction+" "+ input.get("R2Name");
        }
        else{
            instruction = instruction+ " "+ input.get("R1Name");
            int imm= binToInt(input.get("IMM"),6);;
            instruction = instruction+ " "+ imm;
        }
        System.out.println("EXECUTING INSTRUCTION: "+instruction);

    }

  public static void main (String[] args)throws IOException{
     //to run program, paste it in the "program" file in src folder then change the path accordingly
     readFile("C:\\Users\\Habiba\\workspace\\proj-master\\src\\program");
      runProgram();
      System.out.println("REGISTERS");
      System.out.println("----------------------------------");
      Set<String> setOfKeys = registerFile.registers.keySet();
      for (String key : setOfKeys) {
          System.out.println(key +": "+registerFile.registers.get(key));
      }
      System.out.println("----------------------------------");
      System.out.println("STATUS REGISTER");
      System.out.println("V flag: "+ SREG.V);
      System.out.println("Z flag: "+SREG.Z);
      System.out.println("C flag: "+SREG.C);
      System.out.println("N flag: "+SREG.N);
      System.out.println("S flag: "+SREG.S);
      System.out.println("");
      System.out.println("INSTRUCTION MEMORY");
      System.out.println("----------------------------------");
      for (int i=0 ;i<Instruction.pointer;i++)
          System.out.println("INSTRUCTION AT ADDRESS "+ i+ ": "+ Instruction.memory[i]);
      System.out.println("-----------------------------------");
      System.out.println("DATA MEMORY");
      System.out.println("-----------------------------------");
      for (int i=0 ;i<data.memory.length;i++)
          if(data.memory[i]!=null)
          System.out.println("DATA AT ADDRESS "+ i+ ": "+ data.memory[i]);








}

    }
