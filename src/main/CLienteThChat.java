package main;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteThChat {

	private static final long serialVersionUID = 1L;

	private Socket socket;
	private OutputStream ou;
	private Writer ouw;
	private BufferedWriter bfw;
	private String nome;


	//construtor que permite que o cliente digite seu nome
	public ClienteThChat() {

		Scanner in = new Scanner(System.in);
		System.out.print("Diga seu nome: ");
		this.nome = in.nextLine();

	}

	public static void main(String[] args) throws IOException {
		
		ClienteThChat clienteTh = new ClienteThChat();
		clienteTh.conectar();
		
		Thread escuta = new Thread(()->{
					
			try {
					clienteTh.escutar();
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Erro na thread do cliente");
				}

		});
		
		escuta.start();
		
		Scanner in = new Scanner(System.in);
		System.out.print("Digite a mensagem: ");
		String mensagem = in.nextLine();
		
		while(!mensagem.equalsIgnoreCase("sair")) {
			clienteTh.enviarMensagem(mensagem);
			
			System.out.print("Digite a mensagem: ");
			mensagem = in.nextLine();
			
		}
		
		clienteTh.sair();

	}

	public void conectar() throws IOException {

		//estabelece a conexao
		socket = new Socket("localhost", 1234);

		ou = socket.getOutputStream();
		ouw = new OutputStreamWriter(ou);
		bfw = new BufferedWriter(ouw);

		//envia o nome do cliente para o servidor
		bfw.write(this.nome + "\r\n");
		bfw.flush();
	}

	public void enviarMensagem(String msg) throws IOException {

		if (msg.equalsIgnoreCase("Sair")) {

			bfw.write("Desconectado \r\n");
			System.out.println("Desconectado");
		} else {

			//envia a mensagem do cliente para o servidor de bate papo
			bfw.write(msg + "\r\n");
			System.out.println("\n" + this.nome + " diz -> " + msg + "\r\n");
		}
		bfw.flush();

	}

	public void escutar() throws IOException {

		InputStream in = socket.getInputStream();
		InputStreamReader inr = new InputStreamReader(in);
		BufferedReader bfr = new BufferedReader(inr);
		String msg = "";

		while (!"Sair".equalsIgnoreCase(msg)) {

			if (bfr.ready()) {
				msg = bfr.readLine();
				
				if (msg.equals("Sair")) {
					System.out.println("Servidor caiu! \r\n");
					
				}else {
					System.out.println("\n" + msg + "\r\n");
				}
			}

		}
		System.out.println("\n\nFINALIZEI\n\n");
		System.exit(0);
	}

	public void sair() throws IOException {

		enviarMensagem("Sair");
		bfw.flush();
		bfw.close();
		ouw.close();
		ou.close();
		socket.close();
	}
	
}
