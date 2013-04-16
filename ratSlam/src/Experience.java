// Used to setup experience map

public class Experience 
{
	static final double PI = Math.PI;
	
	// Variables
	double x_pc;
	double y_pc;
	double th_pc;
	double x_m;
	double y_m;
	double facing_rad;
	int vt_id;
	int numlinks;
	Link[] links;
	int id;
	public Experience()
	{
		
	}
	
	public Experience(int id, double xPc, double yPc, double thPc, double xM, double yM, double facRad, int vtId, int nLinks, Link[] l)
	{
		this.id = id;
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
