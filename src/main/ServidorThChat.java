package main;

import java.io.BufferedWriter;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServidorThChat extends Thread{
	
	private static ArrayList<BufferedWriter> clientes;
	private static ServerSocket server;
	private String nome;
	private Socket con;
	private InputStream in;
	private InputStreamReader inr;
	private BufferedReader bfr;

	public ServidorThChat(Socket con) {
		this.con = con;
		try {
			in = con.getInputStream();
			inr = new InputStreamReader(in);
			bfr = new BufferedReader(inr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		
		try{
		   
		    server = new ServerSocket(1234);
		    clientes = new ArrayList<BufferedWriter>();
		  
		    System.out.println("Servidor ativo ");
		    
		    while(true){
		       System.out.println("Aguardando conexão...");
		       Socket con = server.accept();
		       System.out.println("Cliente conectado...");
		       Thread t = new ServidorThChat(con);
		       t.start();   
		    }
		                              
		  }catch (Exception e) {
		    
		    e.printStackTrace();
		  }   

	}

	@Override
	public void run() {
		try {
			
			String msg;
			OutputStream out = this.con.getOutputStream();//enviar dados para o cliente (writer)
			Writer ouw = new OutputStreamWriter(out);
			BufferedWriter bfw = new BufferedWriter(ouw);//objeto de escrita nos clientes
			
			clientes.add(bfw);//list de BufWriters permite que o servidor escreva nos clientes
			
			nome = msg = bfr.readLine();//recebe dados do cliente (reader)
			System.out.println("\n" + nome.toUpperCase() + " está no chat");
			
			while (!"Sair".equalsIgnoreCase(msg) || msg != null) {
				
				//mensagem enviada pelo cliente da thread
				msg = bfr.readLine();
				
				//envia a mensagem para todos os clientes do BP e enviar tbm o bfw de quem enviou a mensagem
				enviarAll(bfw, msg);
				System.out.println(nome + " -> " + msg);
				
			}
			if("Sair".equalsIgnoreCase(msg) || msg == null) {
				enviarAll(bfw, "Saiu do bate papo");
				clientes.remove(bfw);
				bfw.close();
			}

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	public void enviarAll(BufferedWriter bwSaida, String msg) throws IOException {
		BufferedWriter bwS;

		//envia a mensagem para todos os clientes do BP exceto o cara que enviou
		for (BufferedWriter bw : clientes) {
			bwS = (BufferedWriter) bw;
			
			//garantir que a mensagem seja enviada para todos do BP exceto para quem a enviou
			if (!(bwSaida == bwS)) {
				bw.write(nome + " -> " + msg + System.lineSeparator());
				bw.flush();
			}else {
				bw.write("Sair");
				bw.flush();
			}
		}
	}

}