

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.io.BufferedReader;
import java.io.FileReader;

public class Trace 
{
	private List<Long> addresses;
	private Set<Long> tree;
	public Trace (String filename)
	{
		String line;
		String token[];
		addresses = new ArrayList<Long>();
		tree = new TreeSet<Long>();
		try
		{
			FileReader fileReader = new FileReader (filename);
			BufferedReader bufferedReader = new BufferedReader (fileReader);
			while ((line = bufferedReader.readLine()) != null)
			{
				token = line.split(" ");
				long blk = Long.parseLong(token[0]);
				long numReq = Long.parseLong(token[1]);
				for (long i = blk; i < (blk+numReq); ++i)
				{
					addresses.add(i);
					tree.add(i);
				}
			}
			bufferedReader.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public List<Long> getAddresses() 
	{
		return new ArrayList<Long>(addresses);
	}
	public static void main (String args[])
	{
		new Trace("P3.lis");
	}
}

