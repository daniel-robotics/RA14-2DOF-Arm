import java.io.*;

public class FileReader
{
	private FileInputStream in;
	private int maxLen = 20;
	private char[] line;
	private boolean eof = false;

	public FileReader(File f, int maxLineLength) throws FileNotFoundException
	{
		in = new FileInputStream(f);
		this.maxLen = maxLineLength;
		line = new char[maxLen];
	}

	private int i, c;
	public String readLine() throws IOException
	{
		for(i=0; i<maxLen; i++)
		{
			c = in.read();
			if(c==-1)
			{
				eof = true;
				break;
			}
			else if(c=='\n' || c=='\r')
			{
				in.read();	//for some reason end-of-lines count as two invisible characters, so after encountering the first, skip the second as well
				break;
			}
			line[i] = (char)c;
		}
		if(!eof)
			return new String(line, 0, i);
		else
			return null;
	}

	public void close()
	{
		try{
			in.close();
		}catch(IOException e){}
		in = null;
	}
}
