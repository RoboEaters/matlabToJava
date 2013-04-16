// From rs_experience_map_iteration.m
public class Exp_Map_Iteration 
{
	// Variables Passed to Class
	int vt_id, prev_vt_id; 
	double vtrans, vrot, x_pc, y_pc, th_pc;
	VT[] vt;
	boolean link_exists;
	//Experience exps = new Experience();
	// VT exps = new VT()
	
	// Constants
	static final double PI = Math.PI;
	
	// Variables Needed
	double accum_delta_x;
	double accum_delta_y;
	double accum_delta_facing;
	double delta_pc;
	int currExpId, newExpId, prevExpId, prev_exp_id, curr_exp_id;
	Experience[] exps;
	int numExps;
	int matched_exp_id, matched_exp_count;
	int[] exp_history = {};
	
	static int PC_DIM_XY = 61;
	static int PC_DIM_TH = 36;
	
	// figure out where the initial value is
	static int EXP_DELTA_PC_THRESHOLD = 0;
	static int EXP_CORRECTION, EXP_LOOPS;
	
	
	public Exp_Map_Iteration(int vt_id, double vtrans, double vrot, double x_pc, double y_pc, double th_pc, VT[] vt)
	{
		this.vt_id = vt_id;
		this.vtrans = vtrans;
		this.vrot = vrot;
		this.x_pc = x_pc;
		this.y_pc = y_pc;
		this.th_pc = th_pc;
		this.vt = vt;
	}

	public void iteration()
	{
		delta_pc = Math.sqrt(get_min_delta(exps[currExpId].x_pc, x_pc, (PC_DIM_XY * PC_DIM_XY)) + 
				get_min_delta(exps[currExpId].y_pc, y_pc, Math.pow(PC_DIM_XY, 2)) +
						get_min_delta(exps[currExpId].th_pc, th_pc, Math.pow(PC_DIM_TH, 2)));
		// if the vt is new or the pc x,y,th has changed enough, create a new experience
		if (vt[vt_id].numexps == 0 || delta_pc > EXP_DELTA_PC_THRESHOLD)
		{
			numExps++;
			create_new_exp(currExpId, newExpId);
			
			prevExpId = currExpId;
			currExpId = numExps;
			
			accum_delta_x = 0;
			accum_delta_y = 0;
			accum_delta_facing = exps[currExpId].facing_rad;
		}
		// where is prev_vt_id initialized??
		else if (vt_id != prev_vt_id)
		{
			matched_exp_count = 0;
			matched_exp_id = 0;
			
			for (int search_id = 0; search_id < vt[vt_id].numexps; search_id++)
			{
				// Ask Prof Krichmar about this section
				// exps[search_id].id
				
			}
		}
		
		if (matched_exp_count > 1)
		{
			// nothing is done here?
		}
		else
		{
			// [min_delta, min_delta_id] = Math.min(delta_pc)
			int min_delta = 5; // random value
			int min_delta_id = 1; // random value
			if (min_delta < EXP_DELTA_PC_THRESHOLD)
			{
				matched_exp_id = vt[vt_id].exps[min_delta_id].id;
				
				// see if the prev exp already has a link to the current exp
				link_exists = false;
				for (int link_id = 0; link_id < exps[currExpId].numlinks; link_id++)
				{
					if (exps[currExpId].links[link_id].exp_id == matched_exp_id)
					{
						link_exists = true;
						break;
					}
				}
				
				if (!link_exists)
				{
					exps[currExpId].numlinks++;
					exps[currExpId].links[exps[currExpId].numlinks].exp_id = matched_exp_id;
					exps[currExpId].links[exps[currExpId].numlinks].d = 
							Math.sqrt(Math.pow(accum_delta_x, 2) + Math.pow(accum_delta_y, 2));
					exps[currExpId].links[exps[currExpId].numlinks].heading_rad = 
							get_signed_delta_rad(exps[currExpId].facing_rad, 
									Math.atan2(accum_delta_y, accum_delta_x));
					exps[currExpId].links[exps[currExpId].numlinks].facing_rad = 
							get_signed_delta_rad(exps[currExpId].facing_rad, 
									accum_delta_facing);
				}
			}
			
			// if there wasn't an experience with the current vt and the posecell x y th
			// then create a new experience
			if (matched_exp_id == 0)
			{
				numExps++;
				create_new_exp(currExpId, numExps);
				matched_exp_id = numExps;
			}
			
			prev_exp_id = currExpId;
			curr_exp_id = matched_exp_id;
			
			accum_delta_x = 0;
			accum_delta_y = 0;
			accum_delta_facing = exps[curr_exp_id].facing_rad;
		}
		
		for (int i = 0; i < EXP_LOOPS; i++)
		{
			for(int exp_id = 0; exp_id < numExps; exp_id++)
			{
				for (int link_id = 0; link_id < exps[exp_id].numlinks; link_id++)
				{
					int e0 = exp_id;
					int e1 = exps[exp_id].links[link_id].exp_id;
					
					double lx = exps[e0].x_m + exps[e0].links[link_id].d * 
							Math.cos(exps[e0].facing_rad) + 
							exps[e0].links[link_id].heading_rad;
					double ly = exps[e0].y_m + exps[e0].links[link_id].d * 
							Math.sin(exps[e0].facing_rad) + 
							exps[e0].links[link_id].heading_rad;
					
					exps[e0].x_m = exps[e0].x_m + (exps[e1].x_m - lx) * EXP_CORRECTION;
					exps[e0].y_m = exps[e0].y_m + (exps[e1].y_m - ly) * EXP_CORRECTION;
					exps[e1].x_m = exps[e1].x_m + (exps[e1].x_m - lx) * EXP_CORRECTION;
					exps[e1].y_m = exps[e1].y_m + (exps[e1].y_m - ly) * EXP_CORRECTION;
					
					double df = get_signed_delta_rad((exps[e0].facing_rad + 
							exps[e0].links[link_id].facing_rad), exps[e1].facing_rad);
					
					exps[e0].facing_rad = clip_rad_180(exps[e0].facing_rad + 
							(df * EXP_CORRECTION));
					exps[e1].facing_rad = clip_rad_180(exps[e1].facing_rad - 
							(df * EXP_CORRECTION));
				}
			}
		}
		
		int newLength = exp_history.length + 1;
		int[] new_exp_history = new int[newLength];
		for (int i = 0; i < newLength; i++)
		{
			new_exp_history[i] = exp_history[i];
		}
		new_exp_history[newLength + 1] = currExpId;
		exp_history = new_exp_history;
	}
			
	// Create a new experience map and add the current experience map onto it
	private void create_new_exp(int curr_exp_id, int new_exp_id)
	{
		
		exps = new Experience[]{};
		exps[curr_exp_id].numlinks++;
		exps[curr_exp_id].links[exps[curr_exp_id].numlinks].exp_id = new_exp_id;
		exps[curr_exp_id].links[exps[curr_exp_id].numlinks].d = Math.sqrt(accum_delta_x * 
				accum_delta_x + (accum_delta_y * accum_delta_y));
		exps[curr_exp_id].links[exps[curr_exp_id].numlinks].heading_rad =
				get_signed_delta_rad(exps[curr_exp_id].facing_rad, 
						Math.atan2(accum_delta_y, accum_delta_x));
		exps[curr_exp_id].links[exps[curr_exp_id].numlinks].facing_rad = 
				get_signed_delta_rad(exps[curr_exp_id].facing_rad, accum_delta_facing);
		
		exps[new_exp_id].x_pc = x_pc;
		exps[new_exp_id].y_pc = y_pc;
		exps[new_exp_id].th_pc = th_pc;
		exps[new_exp_id].vt_id = vt_id;
		exps[new_exp_id].x_m = exps[curr_exp_id].x_m + accum_delta_x;
		exps[new_exp_id].y_m = exps[curr_exp_id].y_m + accum_delta_y;
		exps[new_exp_id].facing_rad = clip_rad_180(accum_delta_facing);
		exps[new_exp_id].numlinks = 0;
		exps[new_exp_id].links = new Link[]{};
		
		// add this experience id to the vt for efficient lookup
		vt[vt_id].numexps++;
		vt[vt_id].exps[vt[vt_id].numexps].id = new_exp_id;
		
	}
	
	// Clip the input angle to between 0 and 2pi radians 
	private double clip_rad_360(double angle)
	{
		while (angle < 0)
		{
			angle = angle + (2 * PI);
		}
		while (angle >= (2 * PI))
		{
			angle = angle - (2 * PI);
		}
		return angle;
	}
	
	// Clip the input angle to between -pi and pi
	private double clip_rad_180(double angle)
	{
		while (angle > PI)
		{
			angle -= PI;
		}
		while (angle <= (-1 * PI))
		{
			angle += (2 * PI);
		}
		return angle;
	}
	
	// Get the minimum delta distance between two values assuming a wrap to zero at max
	private double get_min_delta(double d1, double d2, double max)
	{
		return Math.min(Math.abs(d1 - d2), max - Math.abs(d1 - d2));
	}
	
	// Get signed delta angle from angle1 and angle2 handling the wrap from 2pi to 0
	private double get_signed_delta_rad(double angle1, double angle2)
	{
//		double angle = 0;
//		double dir = angle1 - angle2;
//		if (dir > PI)
//		{
//			dir -= (2 * PI);
//		}
//		else if (dir <= (-1 * PI))
//		{
//			dir += 2 * PI;
//		}
//		
//		if (angle1 <0)
//		{
//			angle1 += (2 * PI);
//		}
//		else if (angle1 >= (2*PI))
//		{
//			angle1 -= (2* PI);
//		}
//		
//		if (angle2 < 0)
//		{
//			angle2 += (2 * PI);
//		}
//		else if (angle2 >= (2 * PI))
//		{
//			angle2 -= (2 * PI);
//		}
//		
//		double delta_angle = Math.abs(angle1 - angle2);
//		
//		if (delta_angle < (2 * PI - delta_angle))
//		{
//			if (dir > 0)
//			{
//				angle = delta_angle;
//			}
//			else
//			{
//				angle = -1 * delta_angle;
//			}
//		}
//		else
//		{
//			if (dir > 0)
//			{
//				angle = 2 * PI - delta_angle; 
//			}
//			else
//			{
//				angle = -1 * (2 * PI - delta_angle);
//			}
//		}
		double angle = 0;
		double dir = clip_rad_180(angle2 - angle1);
		double delta_angle = Math.abs(clip_rad_360(angle1) - clip_rad_360(angle2));
		if (delta_angle < (2 * PI - delta_angle))
		{
			if (dir > 0)
			{
				angle = delta_angle;
			}
			else
			{
				angle = -1 * delta_angle;
			}
		}
		else
		{
			if (dir > 0)
			{
				angle = 2 * PI - delta_angle;
			}
			else
			{
				angle = -1 * (2 * PI - delta_angle);
			}
		}
		return angle;
	}
}
