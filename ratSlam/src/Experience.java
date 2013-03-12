// Used to setup exp
// Similar to VT

public class Experience 
{
	static final double PI = Math.PI;
	
	// initial arguments
	int x_pc;
	int y_pc;
	int th_pc;
	int x_m;
	int y_m;
	double facing_rad;
	int vt_id;
	int numlinks;
	int[] links;
	
	
	public Experience(int xPc, int yPc, int thPc, int xM, int yM, double facRad, int vtId, int nLinks, int[] l)
	{
		x_pc = xPc;
		y_pc = yPc;
		th_pc = thPc;
		x_m = xM;
		y_m = yM;
		facing_rad = facRad;
		vt_id = vtId;
		numlinks = nLinks;
		links = l;
	}
}
