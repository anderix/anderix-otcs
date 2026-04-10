namespace Anderix.OTCS {
	
using System;
using System.IO;
using System.Collections.Generic;

public class ImportWriter {
	private string outfile = "controlfile";
	private int batchmax = 1000;
	private int batch = 0;
	private int filecounter = 0;
	private List<string> fullPathAndFilename = new List<string>();
	private List<string> contentServerPath = new List<string>();
	private List<DateTime> created = new List<DateTime>();
	private List<DateTime> modified = new List<DateTime>();

	public ImportWriter() {
	
	}
	
	public ImportWriter(string outputFile) {
		this.outfile = outputFile;
	}
	
	public ImportWriter(string outputFile, int maxPerBatch) {
		this.outfile = outputFile;
		this.batchmax = maxPerBatch;
	}

	public void Append(string fullPathAndFilename, string contentServerPath, DateTime created, DateTime modified) {
		this.fullPathAndFilename.Add(fullPathAndFilename);
		this.contentServerPath.Add(contentServerPath);
		this.created.Add(created);
		this.modified.Add(modified);
	}
	
	public void Write() {
		StreamWriter sw = new StreamWriter(outputfile());
		sw.WriteLine("<?xml version='1.0' encoding='UTF-8' standalone='no'?>\n<import>");
		string[] files = this.fullPathAndFilename.ToArray();
		string[] cslocations = this.contentServerPath.ToArray();
		DateTime[] createdDTs = this.created.ToArray();
		DateTime[] modifiedDTs = this.modified.ToArray();
			for ( int i = 0; i < files.Length; i++ ) {
				if ( ++this.filecounter % this.batchmax == 0 ) {
					sw.WriteLine("</import>");
					sw.Dispose();
					sw = null;
					batch++;
					sw = new StreamWriter(outputfile());
					sw.WriteLine("<?xml version='1.0' encoding='UTF-8' standalone='no'?>\n<import>");
				}
				sw.WriteLine("\t<node action='create' type='document'>");
				sw.WriteLine("\t\t<file>" + files[i].Replace("&", "&amp;") + "</file>");
				sw.WriteLine("\t\t<location><![CDATA[" + cslocations[i] + "]]></location>");
				sw.WriteLine("\t\t<created><![CDATA[" + createdDTs[i].ToString("yyyyMMddHHmmss") + "]]></created>");
				sw.WriteLine("\t\t<modified><![CDATA[" + modifiedDTs[i].ToString("yyyyMMddHHmmss") + "]]></modified>");
				sw.WriteLine("\t</node>");
			}
		sw.WriteLine("</import>");
		sw.Dispose();
	}
	
	private string outputfile() {
		string justfilename = "";
		if ( outfile.EndsWith(".xml") ) {
			justfilename = outfile.Substring(0, outfile.Length-4);
		} else {
			justfilename = outfile;
		}
		return justfilename + "_" + batch.ToString() + ".xml";
	}
	
}
}