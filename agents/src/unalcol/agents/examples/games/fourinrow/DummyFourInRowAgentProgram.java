/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.examples.games.fourinrow;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;

/**
 *
 * @author Jonatan
 */
public class DummyFourInRowAgentProgram implements AgentProgram {
    protected String color;
    public DummyFourInRowAgentProgram( String color ){
        this.color = color;        
    }
    
    @Override
    public Action compute(Percept p) {        
        long time = (long)(200 * Math.random());
        try{
           Thread.sleep(time);
        }catch(Exception e){}
        if( p.getAttribute(FourInRow.TURN).equals(color) ){
        	int n = Integer.parseInt((String)p.getAttribute(FourInRow.SIZE));
            int i, j;
            boolean flag;
            do {
                i = (int)(n*Math.random());
                j = (int)(n*Math.random());
                flag = (i==n-1) || !p.getAttribute((i+1)+":"+j).equals((String)FourInRow.SPACE);
                flag &= p.getAttribute(i+":"+j).equals((String)FourInRow.SPACE);
            } while ( !flag );
            return new Action( i+":"+j+":"+color );
        }
        return new Action(FourInRow.PASS);
    }

    @Override
    public void init() {
    }
    
}