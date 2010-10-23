package cdveljar;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.ComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListDataListener;

public class Kvasialbum implements ComboBoxModel{

	private ArrayList<String> album;
	private String[] albumString, dirs,artist,utvald;
	private String dir;
	private Main main;
	private int valdNr;
	protected static final String tomMelding = "D'oh, her var det inga musikk-mapper. Prøv igjen?";
	private Enkeltalbum enkeltalbum;

	/*
	 * Get & set
	 */
	public ArrayList<String> getAlbum() {
		return album;
	}
	public void setAlbum(ArrayList<String> album) {
		this.album = album;
	}

	public String[] getDirs() {
		return dirs;
	}
	public void setDirs(String[] dirs) {
		this.dirs = dirs;
	}


	public String getUtvald() {
		return enkeltalbum.getDir();
	}
	public void setUtvald(String[] utvald) {
		this.utvald = utvald;
	}

	public Enkeltalbum getEnkeltalbum() {
		return enkeltalbum;
	}

	public boolean nameCheck (String sa) {
		String s = sa.toLowerCase();
		String[] filendingar = {"ini","jpg","jpeg","png","html","txt","nfo","mov","wma","db",
				"zip","exe","cr2","psd","nef","deb","desktop","xcf","php","py","pyc","bmp",
				"pdf","rar","asf","mp4","doc","docx","gif","tar.gz"};
		for (int lydending = 0; lydending < Sang.lydendingar.length; lydending++) {
			if (s.endsWith(Sang.lydendingar[lydending])) {
				return false;
			}
		}
		for (int filending = 0; filending < filendingar.length; filending++) {
			if (s.endsWith("." + filendingar[filending])) {
				return false;
			}
		}
		return true;
	}

	/*
	 * Konstruktør
	 */
	public Kvasialbum(Main main) {
		this.main = main;
		valdNr = 0;
		albumString = new String[0];
		album = new ArrayList<String>();
		enkeltalbum = new Enkeltalbum(main);
	}

	/*
	 * Startar ei ny plate
	 */
	protected void fix () {
		main.refreshJList();
		album = new ArrayList<String>();
		enkeltalbum.setSanger(new Sang());
		enkeltalbum.nullstillSpelarno();
		for (int i = 0; i < dirs.length; i++) {
			dir = dirs[i];
			try {
				artist = Fillister.dirlist(dir, false);

				// for kvar artist
				for (int artistnr = 0; artistnr < artist.length; artistnr++) {
					String dirArtist = dir +"/" +artist[artistnr];
					try {
						String[] filerArtist = Fillister.dirlist(dirArtist, false);
						for (int albumnr = 0; albumnr<filerArtist.length; albumnr++) {
							boolean ok = nameCheck(filerArtist[albumnr]);
							if (ok) {
								album.add(dir + " --- " +artist[artistnr] +" --- " +filerArtist[albumnr]);
							}
						}
					}
					catch (Exception e) {
						continue;
					}
				}
			}
			catch (Exception e) {
			}
		}
		albumString = new String[album.size()];

		for (int i = 0; i < album.size(); i++) {
			albumString[i] = album.get(i);
		}

		if (albumString.length < 1) {
			System.out.println(tomMelding);
			JOptionPane.showMessageDialog(null, tomMelding);
			System.exit(0);
		}
		else {	
			Random r = new Random();
			valdNr = r.nextInt(albumString.length-1);
			utvald = albumString[valdNr].split(" --- ");			
			boolean b = enkeltalbum.fix(utvald[0],utvald[1],utvald[2]);
			if (b) {}
			else {
				fix();
			}
		}
	}

	/*
	 * Spelar neste sang - viss det er fleire songar på albumet
	 */
	public void nestesang() {
		if (!enkeltalbum.tilfeldigsang) {
			enkeltalbum.aukSpelarno();
			if (enkeltalbum.getSpelarno() < enkeltalbum.getSanger().getOrigfilnamn().length) {
				main.spelSang();
			}
			else {
				JOptionPane.showMessageDialog(main, "Plata er ferdig");
				fix();
			}
		}
		else {
			Random r = new Random();
			int rand = r.nextInt(enkeltalbum.getSanger().getAntalsongar());
			enkeltalbum.setSpelarno(rand);
			main.spelSang();
		}
	}
	public Object getSelectedItem() {
		if (albumString.length > valdNr) {
			String[] temp = albumString[valdNr].split(" --- ");
			return temp[1] + " - " + temp[2];
			//return albumString[valdNr];
		}
		else {
			return null;
		}
	}
	public void setSelectedItem(Object anItem) {		
		String[] temp = ((String) anItem).split(" --- ");
		utvald[1] = temp[0];
		utvald[2] = temp[1];
		for (int i = 0; i < albumString.length; i++) {
			if (albumString[i].contains((String)anItem)) {
				valdNr = i;
				break;
			}
		}
		enkeltalbum.fix(utvald[0], utvald[1], utvald[2]);
		main.refreshJList();
		main.getJList().setSelectedIndex(enkeltalbum.getSpelarno());
	}
	public Object getElementAt(int arg0) {
		if (albumString.length > arg0) {
			String[] temp = albumString[arg0].split(" --- ");
			return temp[1] + " --- " + temp[2];
			//return albumString[arg0];
		}
		else {
			return null;
		}
	}
	public int getSize() {
		return albumString.length;
	}
	public void removeListDataListener(ListDataListener arg0) {}
	public void addListDataListener(ListDataListener arg0) {}
}