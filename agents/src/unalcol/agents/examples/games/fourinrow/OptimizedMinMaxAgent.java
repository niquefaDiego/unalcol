package unalcol.agents.examples.games.fourinrow;

import java.util.Arrays;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;

public class OptimizedMinMaxAgent implements AgentProgram
{
    private String color;
    private static final int K = 4;
    public static final int DFS_DEPTH = 9;
    
    private static class Segments {
    	private int posI[][] = new int[K*K][K];
    	private int posJ[][] = new int[K*K][K];
    	private int count = 0;
    	
    	public void add ( int si, int sj, int di, int dj ) {
    		for ( int k = 0; k < K; ++k ) {
	    		posI[count][k] = si + di*k;
	    		posJ[count][k] = sj + dj*k;
    		}
    		count++;
    	}
    
    	public boolean check(int[][] board, int player) {
    		boolean ok;
    		for ( int seg = 0; seg < count; ++seg ) {
    			ok = true;
    			for ( int k = 0; k < K; ++k )
    				if ( board[posI[seg][k]][posJ[seg][k]] != player ) {
    					ok = false;
    					break;
    				}
    			if ( ok )
    				return true;
    		}
    		return false;
    	}
    }
    
	private boolean firstCompute = true;
	private int n = -1;
	private int[][] board;
	private int[] row;
	private Segments[][] segments;
	private int bestMove = -1;
	
	private void onFirstCompute(Percept p) {
		n = Integer.parseInt((String)p.getAttribute(FourInRow.SIZE));
		board = new int[n][n];
		segments = new Segments[n][n];
		row = new int[n];

		for ( int i = 0; i < n; ++i )
			for ( int j = 0; j < n; ++j )
				segments[i][j] = new Segments();
		
		final int di[] = { 1, 1, 0, 1 };
		final int dj[] = { 0, 1, 1, -1 };
		for ( int d = 0; d < di.length; ++d )
			for ( int si = 0; si < n; ++si )
				for ( int sj = 0; sj < n; ++sj )
				{
					int fi = si + (K-1)*di[d];
					if ( fi < 0 || fi >= n ) continue;
					
					int fj = sj + (K-1)*dj[d];
					if ( fj < 0 || fj >= n ) continue;
					
					for ( int k = 0; k < K; ++k ) {
						segments[si + k*di[d]][sj + k*dj[d]].add( si, sj,  di[d], dj[d] );
					}
				}
	}
	
	private void getBoard( Percept p) {
		Arrays.fill(row, -1);
		for ( int i = 0; i < n; ++i ) {
			for ( int j = 0; j < n; ++j ) {
				String tmp = (String)p.getAttribute(i+":"+j);
				if ( tmp == FourInRow.SPACE )
				{
					board[i][j] = 0;
					row[j] = i;
				}
				else if ( tmp == color ) board[i][j] = 1;
				else board[i][j] = -1;
			}
		}
	}
	
	public int minmax ( int player, int depth )
	{
		if ( depth == DFS_DEPTH )
			return 0;
		
		int r = -1;
		boolean gameOver = true;
		for ( int j = 0; j < n; ++j )
			if ( row[j] >= 0 ) {
				gameOver = false;
				board[row[j]][j] = player;
				if ( segments[row[j]][j].check ( board, player ) ) {
					board[row[j]][j] = 0;
					bestMove = j;
					return r = 1;
				}
				row[j]--;
				int option = -minmax(-player,depth+1);
				if ( option > r ) {
					r = option;
					bestMove = j;
				}
				row[j]++;
				board[row[j]][j] = 0;
			}
		
		if ( gameOver ) return 0;
		return r;
	}

	@Override
	public Action compute(Percept p)
	{
		// not my turn
		if( !p.getAttribute(FourInRow.TURN).equals(color) ) 
			return new Action(FourInRow.PASS);
		
		if ( firstCompute ) {
			firstCompute = false;
			onFirstCompute(p);
		}
		
		getBoard(p);
		minmax(1, 0);
		
		System.out.println( color + " moves (" + row[bestMove] + "," + bestMove + ")" );
		return new Action( row[bestMove]+":"+bestMove+":"+color );
	}

	@Override
	public void init() {
		//dificil inicializar si no tengo el tamaño del tablero :c
	}
	
	public OptimizedMinMaxAgent( String color ) {
		this.color = color;
	}
	
}
