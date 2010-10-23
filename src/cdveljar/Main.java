package cdveljar;

/*
 * Todo:
 * 
 * 	Bugfiksing
 * 		- Ikkje alle album får songane vist
 * 		- Blir alle album med? - viss ikkje, korfor ikkje?
 * 		- Tags for FLAC
 * 		- Sei ifrå om manglande stønad for M4A (,WMA? Fleire?)
 * 		- Ikkje alle album får vist songane frå dropdown-lista utan vidare
 * 		- Skikkeleg fleire-mapper-framsyning i dropdown-lista
 * 
 * 	Nye funksjonar
 *		- Vel sang ved å dobbeltklikke / skrive inn nummer
 *		- Speleliste
 *		- Så langt er spelt hittil-bar
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import javazoom.spi.mpeg.sampled.file.tag.MP3Tag;

public class Main extends JPanel implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	private BasicPlayerTest player;
	private static final int breidde = 700, hogde = 300, fontstr = 15;
	private Kvasialbum album;

	// GUI
	private GridBagLayout gbl;
	private JLabel artistlabel, albumlabel, sanglabel;
	private GridBagConstraints c;
	private JButton velny, spel,stopp,pause,forrige,neste,omval;
	private JList jlist;
	private JFileChooser jfc;
	private JCheckBox id3tagbox, tilfeldigsang;
	private JComboBox albumliste;
	private JProgressBar jpb;


	/*
	 * Startar avspelinga av ein sang
	 */
	public void spelSang() {
		//if (player.getPlayer().getStatus() != BasicPlayer.PLAYING) {
		int spelarno = album.getEnkeltalbum().getSpelarno();
		String tobeplayed = album.getUtvald() + "/" +album.getEnkeltalbum().getSanger().getOrigfilnamn()[spelarno]; 
		if (tobeplayed.endsWith(".mp3") || tobeplayed.endsWith(".flac")) {

			try {
				player.getControl().open(new File(tobeplayed));
				player.play(tobeplayed);
				if (player.getPlayer().m_audioFileFormat.getType().toString().contains("MP3")) {
					Long longer = (Long)player.getPlayer().m_audioFileFormat.properties().get("duration");
					System.out.println("AAAA" +player.getPlayer().m_audioFileFormat +"IK");
					longer = longer/1000;
					String temp = String.valueOf(longer);
					int dur = Integer.valueOf(temp);
					System.out.println("Maxdur: " +dur);
					jpb.setMaximum(dur);
				}

				jlist.setSelectedIndex(spelarno);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		/*	if (tobeplayed.endsWith(".flac")) {
			player.play(tobeplayed);
			jlist.setSelectedIndex(spelarno);
		}
		else {
			fix();
			spelSang();
		}*/
		//	}n
	}

	public JComboBox getAlbumliste() {
		return albumliste;
	}
	public BasicPlayerTest getPlayer() {
		return player;
	}
	public JList getJList () {
		return jlist;
	}

	public void refreshJList() {
		CDModell cdmodell = album.getEnkeltalbum().getCdmodell();
		album.getEnkeltalbum().setCdmodell(new CDModell(cdmodell.getArtist(),cdmodell.getAlbum(),cdmodell.getSongar()));
		album.getEnkeltalbum().getCdmodell().addPropertyChangeListener(this);
		jlist.setModel(album.getEnkeltalbum().getCdmodell());		
	}

	public Main(String[] args) {
		album = new Kvasialbum(this);
		album.getEnkeltalbum().getCdmodell().addPropertyChangeListener(this);
		jlist = new JList(album.getEnkeltalbum().getCdmodell());
		gbl = new GridBagLayout();
		player = new BasicPlayerTest();
		setLayout(gbl);
		c = new GridBagConstraints();
		c.ipadx = 40;
		c.ipady = 10;
		c.anchor = GridBagConstraints.WEST;

		settOppGui();

		fileChoose(args);


	}

	public void fileChoose(String[] args) {
		String[] newargs;
		if (args==null) {
			newargs = new String[0];
		} 
		else {
			newargs = args; 
		}
		if (newargs.length == 0) {
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
				album.setDirs(temp);
				album.fix();
			}
			else if (returnVal == JFileChooser.CANCEL_OPTION) {
				System.exit(0);
			}
			else {
				JOptionPane.showMessageDialog(null, Album.tomMelding);
				System.exit(0);
			}
		}
		else {
			album.setDirs(args);
			album.fix();
		}
	}

	public void setJpbValue(int jpbv) {
		jpb.setValue(jpbv);
	}

	public void settOppGui() {


		albumliste = new JComboBox(album);
		albumliste.setPreferredSize(new Dimension(breidde, 30));

		artistlabel = new JLabel(album.getEnkeltalbum().getCdmodell().getArtist());
		artistlabel.setFont(new Font("Monotype Corsiva", 1, fontstr));
		artistlabel.setHorizontalAlignment(SwingConstants.CENTER);
		artistlabel.setAutoscrolls(true);
		c.gridwidth = 9;
		c.gridx = 0;
		c.gridy = 1;
		add(artistlabel,c);

		albumlabel = new JLabel(album.getEnkeltalbum().getCdmodell().getAlbum());
		//albumtittel.setPreferredSize(new Dimension(350,45));
		albumlabel.setHorizontalAlignment(SwingConstants.CENTER);
		albumlabel.setFont(new Font("Monotype Corsiva", 1, fontstr));
		c.gridx = 0;
		c.gridy = 2;
		add(albumlabel,c);

		c.gridy = 3;
		jpb = new JProgressBar();
		jpb.setValue(0);
		jpb.addMouseListener(new BarComp(this));
		add(jpb,c);

		/*	sanglabel = new JLabel(album.getEnkeltalbum().getCdmodell().getSongar()[album.getEnkeltalbum().getSpelarno()]);
		sanglabel.setHorizontalAlignment(SwingConstants.CENTER);
		sanglabel.setFont(new Font("Monotype Corsiva", 1, fontstr));
		c.gridy = 0;*/

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
		omval = new JButton("Omplasser!");
		omval.addActionListener(new velnycomp(this));
		omval.setPreferredSize(d);

		id3tagbox = new JCheckBox("Bruk tags?",album.getEnkeltalbum().id3tag);
		id3tagbox.addActionListener(new velnycomp(this));
		tilfeldigsang = new JCheckBox("Tilfeldig",album.getEnkeltalbum().tilfeldigsang);
		tilfeldigsang.addActionListener(new velnycomp(this));

		c.gridheight = 5;
		c.gridx = 0;
		c.gridy = 6;
		add(sp,c);

		c.gridy = 5;
		c.gridwidth = 9;
		c.gridheight = 1;
		add(albumliste,c);

		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 4;
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
		c.gridx = 7;
		add(tilfeldigsang,c);
		c.gridx = 8;
		add(omval,c);

		player.addPropertyChangeListener(this);

	}

	public void setStatusForPlayingButtons (boolean verdi) {
		spel.setEnabled(verdi);
		pause.setEnabled(verdi);
		stopp.setEnabled(verdi);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Tilfeldig vald album vart");
		frame.setContentPane(new Main(args));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	class velnycomp implements ActionListener {
		Main main;

		public velnycomp(Main main) {
			this.main = main;
		}
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource() == velny) {
				album.getEnkeltalbum().setCdmodell(new CDModell());
				album.getEnkeltalbum().getCdmodell().addPropertyChangeListener(main);
				jlist.setModel(album.getEnkeltalbum().getCdmodell());
				try {
					player.getControl().stop();
				}
				catch (BasicPlayerException e) {
					System.exit(0);
				}
				finally {
					album.fix();
					artistlabel.setText(album.getEnkeltalbum().getCdmodell().getArtist());
					albumlabel.setText(album.getEnkeltalbum().getCdmodell().getAlbum());
					spel.setEnabled(false);
				}
			}
			else if (arg0.getSource()==spel) {
				if (player.getControl() == null) { spelSang(); }
				if (player.getControl().getStatus() == BasicPlayer.PAUSED) {
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
					album.nestesang();
				}
				catch (BasicPlayerException bpe) {}
			}
			else if (arg0.getSource()==forrige) {
				if (album.getEnkeltalbum().getSpelarno() >= 1) {
					try {
						player.getControl().stop();
						spelSang();
					}
					catch (BasicPlayerException bpe) {}
					finally {
						album.getEnkeltalbum().senkSpelarno();
						setStatusForPlayingButtons(true);
						spel.setEnabled(false);
					}
				}
			}
			else if (arg0.getSource()==id3tagbox) {
				if (album.getEnkeltalbum().id3tag) {
					album.getEnkeltalbum().id3tag = false;
					album.getEnkeltalbum().filnamnOppsett();
					refreshJList();
				}
				else {
					album.getEnkeltalbum().id3tag = true;
					album.getEnkeltalbum().getAlbumTags();
					album.getEnkeltalbum().getCdmodell().setSongar(album.getEnkeltalbum().getSanger().getSongar());
					refreshJList();
					album.getEnkeltalbum().nullstillSpelarno();
					spelSang();
				}
			}
			else if (arg0.getSource()==omval) {
				fileChoose(null);
			}
			else if (arg0.getSource() == tilfeldigsang) {
				if (album.getEnkeltalbum().tilfeldigsang) {
					album.getEnkeltalbum().tilfeldigsang = false;
				}
				else {
					album.getEnkeltalbum().tilfeldigsang = true;
				}
			}
		}
	}

	class BarComp implements MouseListener {
		Main main;

		public BarComp(Main main) {
			this.main = main;
		}

		public void mouseClicked(MouseEvent arg0) {
			if (arg0.getSource() == jpb)
				//			if ( (arg0.getX() > jpb.getX() && arg0.getX() < jpb.getX() + jpb.getWidth()) &&
				//				(arg0.getY() > jpb.getY() && arg0.getY() < jpb.getY() + jpb.getHeight()) )
			{
				double newPos = arg0.getX();
				System.out.println("X: " +newPos);
				double now = jpb.getValue();
				System.out.println("Now: " +now);
				double max = jpb.getWidth();
				double maxValue = jpb.getMaximum();
				System.out.println("Max: " +max);
				System.out.println("MaxValue: " +maxValue);
				double prosentdel = (newPos/max);
				System.out.println("prosentdel: " +prosentdel);
				double prosentNow = (now/maxValue);
				System.out.println("prosentNow: " +prosentNow);
				newPos = (max*prosentdel);
				now = max*prosentNow;
				System.out.println("Ny x: " +newPos);
				double nowFrames = main.getPlayer().getPlayer().m_line.getFramePosition();
				System.out.println("nowFrames: " +nowFrames);
				if (prosentNow != 0) {
					double totalFrames = nowFrames/prosentNow;
					System.out.println("totalFrames: " +totalFrames);
					double newFrames = totalFrames*prosentdel;
					System.out.println("newFrames: " +newFrames);
					double diff = newFrames - nowFrames;
					System.out.println("diff: " +diff);
					int intdiff = (int)diff;
					try {
						System.out.println(jpb.getValue());
						jpb.setValue((int)newFrames);
						System.out.println(jpb.getValue());
						main.getPlayer().getPlayer().skipBytes(intdiff);
					} catch (BasicPlayerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else {
					System.out.println(prosentNow);
				}
			}
		}

		public void mouseEntered(MouseEvent arg0) {}
		public void mouseExited(MouseEvent arg0) {}
		public void mousePressed(MouseEvent arg0) {}
		public void mouseReleased(MouseEvent arg0) {}
	}
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == CDModell.ARTIST_PROPERTY) {
			artistlabel.setText(album.getEnkeltalbum().getCdmodell().getArtist());
		}
		if (evt.getPropertyName() == CDModell.ALBUM_PROPERTY) {
			albumlabel.setText(album.getEnkeltalbum().getCdmodell().getAlbum());
		}
		if (evt.getPropertyName() == BasicPlayerTest.EOM_PROPERTY) {
			album.nestesang();
		}
		if (evt.getPropertyName() == BasicPlayerTest.BAR_PROPERTY) {
			Long temp = (Long)evt.getNewValue();
			temp = temp/1000;
			int place = Integer.valueOf(String.valueOf(temp));
			setJpbValue(place);
		}
	}
}