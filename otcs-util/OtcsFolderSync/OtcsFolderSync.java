/**
 * Copyright (c) MMXIX Anderix
 * All Rights Reserved
 */
import java.util.Calendar;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.net.MalformedURLException;
import anderix.otcs.OtcsClient;
import anderix.otcs.OtcsNode;
import anderix.io.TextFile;
import org.apache.commons.lang.StringEscapeUtils;

public class OtcsFolderSync {
	private static OtcsClient otcs;
	private static boolean testMode = false;
	private static String mode;

	public static void main(String[] args) throws Exception {
		if ( args.length < 3 ) {
			System.out.println("Usage: OtcsFolderSync <mode> <folder_id> <local_path> [--test]");
			System.out.println();
			System.out.println("Modes:");
			System.out.println("  sync      Bidirectional sync between OTCS and local folder");
			System.out.println("  upload    Upload newer or missing local files to OTCS");
			System.out.println("  publish   Download newer or missing OTCS files to local folder");
			System.out.println("  deltas    Report differences without making changes");
			System.out.println();
			System.out.println("Options:");
			System.out.println("  --test    Show what would be done without making changes");
			return;
		}

		mode = args[0].toLowerCase();
		if ( !mode.equals("sync") && !mode.equals("upload") && !mode.equals("publish") && !mode.equals("deltas") ) {
			System.out.println("Unknown mode: " + args[0]);
			return;
		}

		long folderId = Long.parseLong(args[1]);
		String localPath = args[2];

		for ( int i = 3; i < args.length; i++ ) {
			if ( "--test".equals(args[i]) ) {
				testMode = true;
			}
		}

		HashMap properties = new TextFile("OtcsFolderSync.properties").readNameValuePairs();
		try {
			otcs = new OtcsClient("http://" + properties.get("server") + "/otcs/llisapi.dll",
				(String) properties.get("username"), (String) properties.get("password"));
		} catch ( IOException e ) {
			System.out.println("Login failure.");
			System.exit(1);
		}

		switch ( mode ) {
			case "sync":
				walkLocal(folderId, localPath);
				walkOtcs(folderId, localPath);
				break;
			case "upload":
				walkLocal(folderId, localPath);
				break;
			case "publish":
				walkOtcs(folderId, localPath);
				break;
			case "deltas":
				walkLocal(folderId, localPath);
				break;
		}
	}

	private static void walkLocal(long folderId, String localPath) throws IOException, ParseException, MalformedURLException {
		File folder = new File(localPath);
		File[] files = folder.listFiles();
		OtcsNode[] nodes = otcs.getChildren(folderId);

		for ( int i = 0; i < files.length; i++ ) {
			if ( files[i].isFile() ) {
				OtcsNode otcsFile = findNodeByName(files[i].getName(), nodes);
				if ( otcsFile == null ) {
					System.out.println("Local file does not exist on OTCS: " + files[i].getPath());
					if ( !mode.equals("deltas") && !testMode ) {
						System.out.print("Uploading...");
						otcs.uploadDocument(folderId, files[i].getName(), files[i]);
						System.out.println("Done");
					}
				} else {
					Calendar localMod = Calendar.getInstance();
					localMod.setTimeInMillis(files[i].lastModified());
					Calendar otcsMod = parseDate(otcsFile.getModifyDate(), "yyyy-MM-dd'T'HH:mm:ss");

					switch ( compareToSeconds(otcsMod, localMod) ) {
						case -1: //local is newer
							System.out.println("Local is newer: " + files[i].getPath());
							if ( !mode.equals("deltas") && !testMode ) {
								//TODO: replace this with adding a version
								System.out.print("Removing...");
								otcs.deleteNode(otcsFile.getId());
								System.out.println("Done");
								System.out.print("Uploading...");
								if ( mode.equals("sync") ) {
									long id = otcs.uploadDocumentAndGetId(folderId, files[i].getName(), files[i]);
									OtcsNode doc = new OtcsNode(id, otcs);
									files[i].setLastModified(parseDate(doc.getModifyDate(), "yyyy-MM-dd'T'HH:mm:ss").getTimeInMillis());
								} else {
									otcs.uploadDocument(folderId, files[i].getName(), files[i]);
								}
								System.out.println("Done");
							}
							break;
						case 1: //OTCS is newer
							if ( mode.equals("sync") ) {
								System.out.println("OTCS is newer: " + files[i].getPath());
								if ( !testMode ) {
									otcs.downloadDocument(otcsFile.getId(), files[i].getPath());
									files[i].setLastModified(otcsMod.getTimeInMillis());
								}
							}
							break;
						case 0: //dates match
							if ( mode.equals("sync") ) {
								System.out.println("Dates match: " + files[i].getPath());
							}
							break;
					}
				}
			} else if ( files[i].isDirectory() ) {
				long subfolderId = findFolderIdByName(files[i].getName(), nodes);
				if ( subfolderId == 0 ) {
					System.out.println("Local folder does not exist on OTCS: " + files[i].getPath());
					if ( !mode.equals("deltas") && !testMode ) {
						System.out.print("Creating...");
						subfolderId = otcs.createFolderAndGetId(folderId, files[i].getName());
						System.out.println("Done");
						walkLocal(subfolderId, files[i].getPath());
					}
				} else {
					walkLocal(subfolderId, files[i].getPath());
				}
			}
		}
	}

	private static void walkOtcs(long folderId, String localPath) throws IOException, ParseException, MalformedURLException {
		OtcsNode[] nodes = otcs.getChildren(folderId);
		for ( int i = 0; i < nodes.length; i++ ) {
			if ( !nodes[i].isContainer() ) {
				File localFile = new File(localPath + File.separator + nodes[i].getName());
				Calendar localMod = Calendar.getInstance();
				localMod.setTimeInMillis(localFile.lastModified());
				Calendar otcsMod = parseDate(nodes[i].getModifyDate(), "yyyy-MM-dd'T'HH:mm:ss");
				if ( compareToSeconds(otcsMod, localMod) >= 1 ) {
					System.out.println("OTCS is newer: " + nodes[i].getName());
					if ( !testMode ) {
						otcs.downloadDocument(nodes[i].getId(), localPath + File.separator + nodes[i].getName());
						localFile.setLastModified(otcsMod.getTimeInMillis());
					}
				}
			} else if ( nodes[i].getType() == 0 ) { // 0 = folder
				File dir = new File(localPath + File.separator + nodes[i].getName());
				if ( !dir.exists() ) {
					System.out.println("Creating local folder: " + dir.getPath());
					dir.mkdir();
				}
				walkOtcs(nodes[i].getId(), dir.getPath());
			}
		}
	}

	private static OtcsNode findNodeByName(String name, OtcsNode[] nodes) {
		OtcsNode rval = null;
		for ( int i = 0; i < nodes.length; i++ ) {
			if ( name.equals(StringEscapeUtils.unescapeJavaScript(nodes[i].getName())) ) {
				rval = nodes[i];
			}
		}
		return rval;
	}

	private static long findFolderIdByName(String name, OtcsNode[] nodes) {
		long rval = 0;
		for ( int i = 0; i < nodes.length; i++ ) {
			if ( nodes[i].getType() == 0 ) { // 0 = folder
				if ( name.equals(StringEscapeUtils.unescapeJavaScript(nodes[i].getName())) ) {
					rval = nodes[i].getId();
				}
			}
		}
		return rval;
	}

	private static Calendar parseDate(String dateStr, String format) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(dateStr));
		return cal;
	}

	private static int compareToSeconds(Calendar a, Calendar b) {
		Calendar ca = (Calendar) a.clone();
		Calendar cb = (Calendar) b.clone();
		ca.set(Calendar.MILLISECOND, 0);
		cb.set(Calendar.MILLISECOND, 0);
		long diff = ca.getTimeInMillis() - cb.getTimeInMillis();
		return diff > 0 ? 1 : (diff < 0 ? -1 : 0);
	}
}
