import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.regex.*;

public class ChatClient {

    // Variáveis relacionadas com a interface gráfica --- * NÃO MODIFICAR *
    JFrame frame = new JFrame("Chat Client");
    private JTextField chatBox = new JTextField();
    private JTextArea chatArea = new JTextArea();
    // --- Fim das variáveis relacionadas coma interface gráfica

    // Se for necessário adicionar variáveis ao objecto ChatClient, devem
    // ser colocadas aqui

    boolean over = false;
    
    // Socket info
    private SocketChannel clientSocket;

    private BufferedReader input;
    
    // Decoder and enconder
    private final Charset charset = Charset.forName("UTF8");
    private final CharsetDecoder decoder = charset.newDecoder();
    private final CharsetEncoder encoder = charset.newEncoder();
    
    // Método a usar para acrescentar uma string à caixa de texto
    // * NÃO MODIFICAR *
    public void printMessage(final String message) {
        chatArea.append(message);
    }

    
    // Construtor
    public ChatClient(String server, int port) throws IOException {

        // Inicialização da interface gráfica --- * NÃO MODIFICAR *
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(chatBox);
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.SOUTH);
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        frame.setSize(500, 300);
        frame.setVisible(true);
        chatArea.setEditable(false);
        chatBox.setEditable(true);
        chatBox.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    try {
			newMessage(chatBox.getText());
		    } catch (IOException ex) {
		    } finally {
			chatBox.setText("");
		    }
		    if (over)
			System.exit(0);
		}
	    });
        // --- Fim da inicialização da interface gráfica

        // Se for necessário adicionar código de inicialização ao
        // construtor, deve ser colocado aqui

	
	try {
	    clientSocket = SocketChannel.open();
	    clientSocket.configureBlocking(true);
	    clientSocket.connect(new InetSocketAddress(server, port));
	} catch (IOException ex) {
	}
    }


    // Método invocado sempre que o utilizador insere uma mensagem
    // na caixa de entrada
    public void newMessage(String message) throws IOException {
        // PREENCHER AQUI com código que envia a mensagem ao servidor
	
	clientSocket.write(encoder.encode(CharBuffer.wrap(message)));
	
    }

    public void printChatMessage(ChatMessage message) {
	printMessage(message.toString(true));
    }
    
    // Método principal do objecto
    public void run() throws IOException {
        // PREENCHER AQUI
	
	try {
	    while (!clientSocket.finishConnect()) {
	    }
	} catch (Exception ce) {
	    System.err.println("Unable to connect to the server... Halting");
	    System.exit(0);
	    return;
	}
	
	input = new BufferedReader(new InputStreamReader(clientSocket.socket().getInputStream()));
	
	while (true) {
	    String message = input.readLine();
	    
	    if (message == null) {
		break;
	    }
	    
	    message = message.trim();
	    
	    printChatMessage(ChatMessage.parseString(message));
	}
	
	clientSocket.close();
	
	try {
	    // To prevent client from closing right away
	    Thread.sleep(73);
	} catch (InterruptedException ie) {
	}
	
	over = true;
    }
    
    
    // Instancia o ChatClient e arranca-o invocando o seu método run()
    // * NÃO MODIFICAR *
    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient(args[0], Integer.parseInt(args[1]));
        client.run();
    }

}
