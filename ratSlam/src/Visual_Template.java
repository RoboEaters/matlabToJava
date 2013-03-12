import java.lang.reflect.Array;

// This class creates the visual template, taking the image 
// and breaking it down into a two-dimensional array
public class Visual_Template 
{
	int IMAGE_Y_SIZE = 100;		// Should be size of viddata
	int IMAGE_X_SIZE = 100;		// Should be size of viddata

	int numvts = 1;
	int prev_vt_id = 1;
	int[] vt_history = {0};

	double VT_GLOBAL_DECAY = 0.1;
	double VT_ACTIVE_DECAY = 1.0;
	double VT_SHIFT_MATCH = 20;
	double VT_MATCH_THRESHOLD = 0.09;
	int[] IMAGE_VT_Y_RANGE = setRange(IMAGE_Y_SIZE);
	int[] IMAGE_VT_X_RANGE = setRange(IMAGE_X_SIZE);

	public Visual_Template(int raw_image[][], int x, int y, int th)
	{

	}

	public void visual_template(int raw_image[][], int x, int y, int th)
	{
		int [][] sub_image = {IMAGE_VT_Y_RANGE,IMAGE_VT_X_RANGE};
		int vt_id = 0;

		// normalized intensity sums
		int[][] image_x_sums = {};

		// Goes through each pixel of the image, sums up the rows
		// and stores the sum of each row into image_x_sums
		for (int i = 0; i < sub_image.length; i++)
		{
			for(int j = 0; j < sub_image[1].length; i++)
			{
				image_x_sums[i][j] = sub_image[i][j];
			}
		}

		// adds all values in image_x_sums
		int sumOf_image_x_sums = 0;
		for (int i = 0; i < image_x_sums.length; i++)
		{
			for (int j = 0; j < image_x_sums[i].length; j++)
				sumOf_image_x_sums = image_x_sums[i][j];
		}

		// divides each value in image_x_sums by total sum
		for (int i = 0; i < image_x_sums.length; i++)
		{
			for(int j = 0; j < image_x_sums[i].length; j++)
				image_x_sums[i][j]  = image_x_sums[i][j] / sumOf_image_x_sums;
		}

		// initialize minOffset and minDif
		int[] minOffset = {};
		int[] minDif = {};

		// change parameters
		VT[] vt = new VT[]{};

		//vt[0] = new VT(1,1,1,1);
		// get vt down before doing this loop
		for (int k = 0; k < numvts; k++)
		{
			//vt.visTemp
			vt[k].template_decay = vt[k].template_decay - VT_GLOBAL_DECAY;
			if(vt[k].template_decay < 0)
			{
				vt[k].template_decay = 0;
			}
			// [min_offset[k], min_diff[k]] = rs_compare_segments(image_x_sums, vt[k].template, VT_SHIFT_MATCH, size(image_x_sums,2) 
		}

		// NEED :: [diff, diff_id] = min(min_diff)
		// for now:
		int diff = 1;
		int diff_id = 1;

		//if this intensity template doesn't match any of the existing templates,
		// then create a new template
		if ((diff * image_x_sums[2].length) > VT_MATCH_THRESHOLD)
		{
			numvts++;
			vt[numvts] = new VT(numvts, image_x_sums, VT_ACTIVE_DECAY, x, y, th, 1, 0, new int[1]);
			vt_id = numvts;
		}
		else
		{
			vt_id = diff_id;
			vt[vt_id].template_decay = VT_ACTIVE_DECAY;
			if (prev_vt_id != vt_id)
			{
				vt[vt_id].first = 0;
			}
		}
		
		// vt_history = [vt_history; vt_id] 
	}

	// Creates 1-dimensional arrays that mimic MATLAB colon operator
	private int[] setRange(int range)
	{
		int[] toReturn = new int[range];
		for (int i = 0; i < range; i++)
		{
			toReturn[i] = i;
		}
		return toReturn;
	}
}
