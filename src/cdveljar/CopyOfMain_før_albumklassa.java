package cdveljar;

/*
 * Todo:
 * 
 * 	Bugfiksing
 * 		- Ikkje alle album får songane vist
 * 		- Blir alle album med? - viss ikkje, korfor ikkje?
 * 		- Tags for FLAC
 * 
 * 	Nye funksjonar
 *		- Mulegheit til å velje album frå ei dropdown-liste
 *		- Mulegheit til å velje førre plate
 *		- Vel sang ved å dobbeltklikke / skrive inn nummer
 *		- Volumjustering
 *
 *	Elegance is not optional
 *		- Kommentering
 *		- Grafisk pirk
 *		- Gå skikkeleg over koden
 *		- Kva som skrives ut
 */

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class CopyOfMain_før_albumklassa extends JPanel implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	private ArrayList<String> album;
	private CDModell cdmodell;
	private Sang sanger;
	private int spelarno;
	private String[] albumString, dirs,artist,utvald;
	private String dir;
	private BasicPlayerTest player;
	private boolean id3tag;
	private static final int breidde = 700, hogde = 300, fontstr = 15;
	private static final String tomMelding = "D'oh, her var det inga musikk-mapper. Prøv igjen?";

	// GUI
	private GridBagLayout gbl;
	private JLabel artistlabel, albumlabel;
	private GridBagConstraints c;
	private JButton velny, spel,stopp,pause,forrige,neste;
	private JList jlist;
	private JFileChooser jfc;
	private JCheckBox id3tagbox;
	private JComboBox albumliste;

	/*
	 * Startar avspelinga av ein sang
	 */
	public void spelSang() {
		//if (player.getPlayer().getMStatus() != BasicPlayer.PLAYING) {
		String tobeplayed = utvald[0] + sanger.getOrigfilnamn()[spelarno]; 
		if (tobeplayed.endsWith(".mp3") || tobeplayed.endsWith(".flac")) {
			player.play(tobeplayed);
			jlist.setSelectedIndex(spelarno);
		}
	/*	if (tobeplayed.endsWith(".flac")) {
			player.play(tobeplayed);
			jlist.setSelectedIndex(spelarno);
		}
		else {
			fix();
			spelSang();
		}*/
		//	}
	}

	/*
	 * Spelar neste sang - viss det er fleire songar på albumet
	 */
	public void nestesang() {
		spelarno++;
		if (spelarno < sanger.getOrigfilnamn().length) {
			spelSang();
		}
		else {
			JOptionPane.showMessageDialog(this, "Plata er ferdig");
			fix();
		}
	}

	public void refreshJList() {
		cdmodell = new CDModell(cdmodell.getArtist(),cdmodell.getAlbum(),cdmodell.getSongar());
		cdmodell.addPropertyChangeListener(this);
		jlist.setModel(cdmodell);		
	}

	public void getAlbumTags() {
		for (int i = 0; i < sanger.getSongar().length; i++) {
			try {
				player.getControl().open(new File(utvald[0] + sanger.getOrigfilnamn()[i]));
				sanger.setSongarIndex(i,i+1 + " - " +player.getPlayer().m_audioFileFormat.properties().get("title"));
			}
			catch (BasicPlayerException bpe) {
				break;
			}
		}
		cdmodell.setSongar(sanger.getSongar());
	}

	public void filnamnOppsett() {
		try {
			String[] filer = Fillister.dirlist(utvald[0], true);
			sanger.setSongar(filer);
			sanger.setOrigfilnamn(filer);

			sanger.fikssanger();

			if (sanger.getAntalsongar()<=0) {
				// viss undermapper: utforsk. elles
				try {
					sanger.setNullteljar(0);
					String[] temp = Fillister.folderlist(utvald[0]);
					Random t = new Random();
					int valdplate = t.nextInt(temp.length-1);
					String dir = temp[valdplate];
					sanger.setSongar(Fillister.dirlist(dir, true));
					sanger.fikssanger();
					cdmodell.setSongar(sanger.getSongar());
					sanger.setOrigfilnamn(sanger.getSongar());
				}
				catch (Exception e) {
					System.out.println("Funka dårleg." +utvald[0]);
					fix();
				}
			}
			else { 
				// Fjern rippegruppe-namn
				sanger.fiksendingar();
				cdmodell.setSongar(sanger.getSongar());
				spelarno=0;
				spelSang();
			}
		}
		catch (Exception e) {
			fix();
		}
	}

	private void fix () {
		refreshJList();
		spelarno=0;
		album = new ArrayList<String>();
		sanger = new Sang();
		for (int i = 0; i < dirs.length; i++) {
			dir = dirs[i];
			try {
				artist = Fillister.dirlist(dir, false);
			}
			catch (Exception e) {
			}

			// for kvar artist
			for (int artistnr = 0; artistnr < artist.length; artistnr++) {
				String dirArtist = dir +"/" +artist[artistnr];
				try {
					String[] filerArtist = Fillister.dirlist(dirArtist, false);
					for (int albumnr = 0; albumnr<filerArtist.length; albumnr++) {
						album.add(dir + " --- " +artist[artistnr] +" --- " +filerArtist[albumnr]);
					}
				}
				catch (Exception e) {
					continue;
				}
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
			int vald = r.nextInt(albumString.length-1);

			// Tweak printout
			utvald = albumString[vald].split(" --- ");			
			// utvald[0] = mappe, utvald[1] = artist, utvald[2] = album
			utvald[0] = "" +utvald[0] +"/" +utvald[1] +"/" +utvald[2] +"/";

			utvald[1] = sanger.kommatriksing(utvald[1]);
			/*if (utvald[1].length() > 40) {
				cdmodell.setArtist(utvald[1].substring(0,40));
			}
			else {
				cdmodell.setArtist(utvald[1]);
			}
			if (utvald[2].length() > 40) {
				cdmodell.setAlbum(utvald[2].substring(0,40));
			}
			else {
				cdmodell.setAlbum(utvald[2]);
			}*/
			cdmodell.setArtist(utvald[1]);
			cdmodell.setAlbum(utvald[2]);

			// All song-triksing går herfrå
			filnamnOppsett();
		}
		if (id3tag){
			getAlbumTags();
			spelarno = 0;
			spelSang();
		}
	}

	public CopyOfMain_før_albumklassa(String[] args) {
		cdmodell = new CDModell();
		cdmodell.addPropertyChangeListener(this);
		jlist = new JList(cdmodell);
		gbl = new GridBagLayout();
		player = new BasicPlayerTest();
		setLayout(gbl);
		c = new GridBagConstraints();
		c.ipadx = 40;
		c.ipady = 10;
		c.anchor = GridBagConstraints.WEST;

		settOppGui();

		if (args.length == 0) {
			jfc = new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			jfc.setMultiSelectionEnabled(true);
			JOptionPane.showMessageDialog(this,"Velkommen. Dette programmet vel eit tilfeldig album for deg. Du skjønner nok koss det verkar, først no bør du velje mappa der du har musikken din","Tilfeldig-album-veljar",1);
			int returnVal = jfc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				String[] temp = new String[jfc.getSelectedFiles().length];
				for (int dir = 0; dir<temp.length; dir++) {
					temp[dir] = jfc.getSelectedFiles()[dir].toString();
				}
				dirs = temp;
				fix();
			}
			else if (returnVal == JFileChooser.CANCEL_OPTION) {
				System.exit(0);
			}
			else {
				JOptionPane.showMessageDialog(null, tomMelding);
				System.exit(0);
			}
		}
		else {
			dirs = args;
			fix();
		}
	}

	public void settOppGui() {

		albumliste = new JComboBox();
		
		artistlabel = new JLabel(cdmodell.getArtist());
		artistlabel.setFont(new Font("Monotype Corsiva", 1, fontstr));
		artistlabel.setAutoscrolls(true);
		c.gridwidth = 9;
		c.gridx = 0;
		c.gridy = 1;
		add(artistlabel,c);

		albumlabel = new JLabel(cdmodell.getAlbum());
		//albumtittel.setPreferredSize(new Dimension(350,45));
		albumlabel.setFont(new Font("Monotype Corsiva", 1, fontstr));
		c.gridx = 0;
		c.gridy = 0;
		add(albumlabel,c);

		JScrollPane sp = new JScrollPane();
		sp.setPreferredSize(new Dimension(breidde, hogde));
		sp.getViewport().add(jlist);

		Dimension d = new Dimension(30,10);

		velny = new JButton();
		velny.setText("Ny!");
		velny.addActionListener(new velnycomp(this));
		velny.setPreferredSize(d);

		spel = new JButton("Spel");
		spel.addActionListener(new velnycomp(this));
		spel.setPreferredSize(d);
		spel.setEnabled(false);
		stopp = new JButton("Stopp");
		stopp.addActionListener(new velnycomp(this));
		stopp.setPreferredSize(d);
		pause = new JButton("Pause");
		pause.addActionListener(new velnycomp(this));
		pause.setPreferredSize(d);
		forrige = new JButton("Førre");
		forrige.addActionListener(new velnycomp(this));
		forrige.setPreferredSize(d);
		neste = new JButton("Neste");
		neste.addActionListener(new velnycomp(this));
		neste.setPreferredSize(d);

		id3tagbox = new JCheckBox("Bruk tags?",id3tag);
		id3tagbox.addActionListener(new velnycomp(this));

		c.gridheight = 6;
		c.gridwidth = 9;
		c.gridx = 0;
		c.gridy = 4;
		add(sp,c);

		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 2;
		add(velny,c);
		c.gridx = 1;
		add(id3tagbox,c);

		c.gridx = 2;
		add(forrige,c);
		c.gridx = 3;
		add(spel,c);
		c.gridx = 4;
		add(pause,c);
		c.gridx = 5;
		add(stopp,c);
		c.gridx = 6;
		add(neste,c);
		
		player.addPropertyChangeListener(this);
		
	}
	
	public void setStatusForPlayingButtons (boolean verdi) {
		spel.setEnabled(verdi);
		pause.setEnabled(verdi);
		stopp.setEnabled(verdi);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Tilfeldig vald album vart");
		frame.setContentPane(new CopyOfMain_før_albumklassa(args));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	class velnycomp implements ActionListener {
		CopyOfMain_før_albumklassa main;

		public velnycomp(CopyOfMain_før_albumklassa main) {
			this.main = main;
		}
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource() == velny) {
				cdmodell = new CDModell();
				cdmodell.addPropertyChangeListener(main);
				jlist.setModel(cdmodell);
				try {
					System.out.println("PLAYER: " +player.getControl());
					player.getControl().stop();
				}
				catch (BasicPlayerException e) {
					System.exit(0);
				}
				finally {
					fix();
					artistlabel.setText(cdmodell.getArtist());
					albumlabel.setText(cdmodell.getAlbum());
					spel.setEnabled(false);
				}
			}
			else if (arg0.getSource()==spel) {
				if (player.getControl() == null) { spelSang(); }
				if (player.getControl().getMStatus() == BasicPlayer.PAUSED) {
					try {
						player.getControl().resume();
					}
					catch (BasicPlayerException bpe) {

					}
				}	
				else {
					spelSang();
				}
				pause.setEnabled(true);
				stopp.setEnabled(true);
				spel.setEnabled(false);
			}
			else if (arg0.getSource()==pause) {
				try {
					player.getControl().pause();
					setStatusForPlayingButtons(true);
					pause.setEnabled(false);
				}
				catch (Exception e) {

				}
			}
			else if (arg0.getSource()==stopp) {
				try {
					player.getControl().stop();
					setStatusForPlayingButtons(false);
					spel.setEnabled(true);
				}
				catch (BasicPlayerException bpe) {
				}
			}
			else if (arg0.getSource()==neste) {
				try {
					player.getControl().stop();
					setStatusForPlayingButtons(true);
					spel.setEnabled(false);
					nestesang();
				}
				catch (BasicPlayerException bpe) {}
			}
			else if (arg0.getSource()==forrige) {
				if (spelarno >= 1) {
					try {
						player.getControl().stop();
						spelSang();
					}
					catch (BasicPlayerException bpe) {}
					finally {
						spelarno--;
						setStatusForPlayingButtons(true);
						spel.setEnabled(false);
					}
				}
			}
			else if (arg0.getSource()==id3tagbox) {
				if (id3tag) {
					id3tag = false;
					filnamnOppsett();
					refreshJList();
				}
				else {
					id3tag = true;
					getAlbumTags();
					cdmodell.setSongar(sanger.getSongar());
					refreshJList();
					spelarno = 0;
					spelSang();
				}
			}
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == CDModell.ARTIST_PROPERTY) {
			artistlabel.setText(cdmodell.getArtist());
		}
		if (evt.getPropertyName() == CDModell.ALBUM_PROPERTY) {
			albumlabel.setText(cdmodell.getAlbum());
		}
		if (evt.getPropertyName() == BasicPlayerTest.EOM_PROPERTY) {
			nestesang();
		}
	}
}