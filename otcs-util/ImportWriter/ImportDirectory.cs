using System;
using Anderix.OTCS;
using ZetaLongPaths;

class ImportDirectory {
	static ImportWriter ctrl = null;

	public static void Main(string[] args) {
		if ( args.Length < 2 ) {
			Console.WriteLine("Arguments are required:");
			Console.WriteLine("1. Path to root (from where source files will be read).");
			Console.WriteLine("2. Content Server path that will replace the root path in the output.");
			Console.WriteLine("3. [optional] Output file.");
			Console.WriteLine("4. [optional] Number of files per batch.");
		} else {
			if ( args.Length == 4 ) {
				ctrl = new ImportWriter(args[2], Int32.Parse(args[3]));
			} else if ( args.Length == 3 ) {
				ctrl = new ImportWriter(args[2]);
			} else {
				ctrl = new ImportWriter();
			}
			WalkDirectoryTree( new ZlpDirectoryInfo(args[0]), args[0], args[1] );
			ctrl.Write();
		}	
	}
	
	static void WalkDirectoryTree(ZlpDirectoryInfo root, string rootPath, string contentserverPath) {
		ZlpFileInfo [] files = null;
		ZlpDirectoryInfo[] subDirs = null;
		files = root.GetFiles("*.*");		
		if ( files != null ) {
			foreach ( ZlpFileInfo fi in files ) {
				DateTime creationTime = fi.CreationTime<fi.LastWriteTime?fi.CreationTime:fi.LastWriteTime;
				ctrl.Append(fi.FullName, fi.DirectoryName.Replace(rootPath, contentserverPath).Replace("\\", ":"), creationTime, fi.LastWriteTime);
			}
			subDirs = root.GetDirectories();

			foreach ( ZlpDirectoryInfo dirInfo in subDirs ) {
				WalkDirectoryTree( dirInfo, rootPath, contentserverPath );
			}
		}			 
	}
	
}