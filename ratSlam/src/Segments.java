// Translation of rs_compare_segments

public class Segments 
{
	int[] seg1, seg2, cdiff;
	int cwl;
	int slen;
	double[] segment;
	double minoffset;
	double ret_offset, sdif;
	
	public Segments(int[] s1, int[] s2, int len, int cwl)
	{
		seg1 = s1;
		seg2 = s2;
		slen = len;
		cwl = this.cwl;
	}
	
	public double[] compare_segments()
	{
		// assume a large difference
		double mindiff = 1000000;
		
		double[] diffs = new double[slen];
		
		// for each offset sum the abs difference between the two segments
		// for offset = 0 : slen
		for (int offset = 0, index = 0; offset < slen; offset = offset + 5, index++)
		{
			cdiff[index] = 0;
			for (int i = offset + 1, j = 0; i < cwl && j < (cwl - offset); i++, j++)
			{
				cdiff[index] += Math.abs(seg1[i] - seg2[j]);
			}
			cdiff[index] = cdiff[index] / (cwl - offset);
			diffs[slen - offset + 1] = cdiff[index];
			if (cdiff[index] < mindiff)
			{
				mindiff = cdiff[index];
				minoffset = offset;
				
			}
		}
		
		// for offset = 1 : slen
		for (int offset = 0, index2 = 0; offset < slen; offset += 5, index2++)
		{
			for (int i = 0, j = 1 + offset; i < (cwl - offset) && j < cwl; i++, j++)
			{
				cdiff[index2] += Math.abs(seg1[i] - seg2[j]);
			}
			cdiff[index2] = cdiff[index2] / (cwl - offset);
			diffs[slen + 1 + offset] = cdiff[index2];
			if (cdiff[index2] < mindiff)
			{
				mindiff = cdiff[index2];
				minoffset = -1 * offset;
			}
		}
		
		ret_offset = minoffset;
		sdif = mindiff;
		return new double[]{ret_offset, sdif};
	}
}
