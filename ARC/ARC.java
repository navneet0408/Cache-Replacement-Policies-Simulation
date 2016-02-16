
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ARC 
{
	private Trace trace;
	private long cacheSize;
	private long p;
	private List<Long> t1, b1, t2, b2;
	private Set<Long> t1h, b1h, t2h, b2h;
	private long numHits;
	private long numMisses;
	public ARC (String traceFileName, long size)
	{
		trace = new Trace (traceFileName);
		cacheSize = size;
		p=0;
		numHits = 0;
		numMisses = 0;
		t1 = new LinkedList<Long>();
		b1 = new LinkedList<Long>();
		t2 = new LinkedList<Long>();
		b2 = new LinkedList<Long>();
		
		t1h = new HashSet<Long>();
		t2h = new HashSet<Long>();
		b1h = new HashSet<Long>();
		b2h = new HashSet<Long>();
		
		for (long address: trace.getAddresses())
		{
			request (address);
		}
		
		System.out.println("\n\nPolicy = ARC Cache Size = " + cacheSize + "  Tracefile = " + traceFileName);
		System.out.println("Num Hits = " + numHits);
		System.out.println("Num Misses = " + numMisses);
		System.out.println("Num Access = " + (numHits + numMisses));
		System.out.println("Hit Ratio = " + (double) numHits/(numHits + numMisses));
		
	}
	
	private void request (long address)
	{
		if (t1h.contains(address))
		{
			numHits++;
			t1.remove(address);
			t1h.remove(address);
			t2.add(0, address);
			t2h.add(address);
		}
		else if (t2h.contains(address))
		{
			numHits++;
			t2.remove(address);
			t2.add(0, address);
		}
		else if (b1h.contains(address))
		{
			numMisses++;
			incP();
			replace (address);
			b1.remove(address);
			b1h.remove(address);
			t2.add(0, address);
			t2h.add(address);
		}
		else if(b2h.contains(address))
		{
			numMisses++;
			decP();
			replace (address);
			b2.remove(address);
			b2h.remove(address);
			t2.add(0, address);
			t2h.add(address);
		}
		else
		{
			numMisses++;
			if ((t1.size() + b1.size()) >= cacheSize)
			{
				if (t1.size() < cacheSize)
				{
					long l = b1.remove(b1.size() -1);
					b1h.remove(l);
					replace (address);
				}
				else
				{
					long l = t1.remove(t1.size()-1);
					t1h.remove(l);
				}
			}
			else
			{
				if((t1.size() + t2.size() + b1.size() + b2.size()) >= cacheSize)
				{
					if( (t1.size() + t2.size() + b1.size() + b2.size()) >= 2*cacheSize)
					{
						long l = b2.remove(b2.size()-1);
						b2h.remove(l);
					}
					replace (address);
				}
			}
			t1.add(0, address);
			t1h.add(address);
		}
		
	}
	
	
	private void decP()
	{
		int delta;
		if(b2.isEmpty())
			delta = (b2.size() > b1.size()) ? 1: (b1.size()/(b2.size()+1));
		else
			delta = (b2.size() > b1.size()) ? 1: (b1.size()/(b2.size()));
		
		p = Math.max(p-delta, 0);
	}
	
	private void incP()
	{
		int delta;
		if (b1.isEmpty())
			delta = (b1.size() > b2.size()) ? 1: (b2.size()/(b1.size()+1));
		else
			delta = (b1.size() > b2.size()) ? 1: (b2.size()/(b1.size()));
		
		p = Math.min(p+delta, cacheSize);
	}
	
	private void replace (long address)
	{
		if ((!t1.isEmpty()) && ((t1.size() > p) || ((t1.size() == p) && (b2h.contains(address)))))
		{
			long l = t1.remove(t1.size()-1);
			t1h.remove(l);
			b1.add(0, l);
			b1h.add(l);
		}
		else
		{
			long l = t2.remove(t2.size()-1);
			t2h.remove(l);
			b2.add(0, l);
			b2h.add(l);
		}
	}
	
	public static void main (String args[])
	{
		try
		{
			if (args.length < 2)
			{
				System.out.println("Invalid arguments");
			}
			int cacheSize = Integer.parseInt(args[0]);
			String fileName = args[1] + ".lis";
			new ARC (fileName, cacheSize);
		}
		catch (Exception e)
		{
			System.out.println(e);
			System.exit(1);
		}
	}
}

