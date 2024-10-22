// src/SnakeGame.java
import javax.swing.*; // Importa componentes da interface gráfica do Swing
import java.awt.*; // Importa classes para manipulação de gráficos
import java.awt.event.ActionEvent; // Importa classe para eventos de ação
import java.awt.event.ActionListener; // Importa interface para ouvir eventos de ação
import java.awt.event.KeyAdapter; // Importa classe para adaptação de eventos de teclado
import java.awt.event.KeyEvent; // Importa classe para eventos de teclado
import java.util.ArrayList; // Importa a classe ArrayList para manipulação de listas dinâmicas
import java.util.Random; // Importa a classe Random para geração de números aleatórios

public class SnakeGame extends JPanel implements ActionListener {
    private final int WIDTH = 600; // Largura da tela do jogo
    private final int HEIGHT = 600; // Altura da tela do jogo
    private final int TILE_SIZE = 30; // Tamanho de cada bloco da cobrinha e comida
    private final ArrayList<Point> snake = new ArrayList<>(); // Lista que representa a cobrinha
    private Point food; // Ponto que representa a comida
    private char direction = ' '; // Direção atual da cobrinha (U, D, L, R)
    private boolean running = false; // Indica se o jogo está em execução
    private Timer timer; // Temporizador para atualizar o jogo
    private JPanel gameOverPanel;
    private JLabel gameOverLabel;
    private JLabel restartLabel;
    
    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT)); // Define o tamanho preferido do painel
        setBackground(Color.YELLOW); // Define a cor de fundo do painel
        setFocusable(true); // Permite que o painel receba foco de entrada
        setLayout(new BorderLayout());
        addKeyListener(new KeyAdapter() { // Adiciona um ouvinte para eventos de teclado
            @Override
            public void keyPressed(KeyEvent e) { 
             
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if(direction != 'D'){
                            direction = 'U'; 
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if(direction != 'U'){
                            direction = 'D';
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        if(direction != 'R'){
                            direction = 'L';
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if(direction != 'L'){
                            direction = 'R';
                        }
                        break;
                    case KeyEvent.VK_SPACE:
                        if(running == false){
                            startGame();
                        }
                        break;
                }
            }
        });
        gameOverPanel = new JPanel();
        gameOverPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        gameOverPanel.setBackground(new Color(0, 0, 0, 100)); // Fundo preto com transparência
        gameOverPanel.setLayout(new GridBagLayout()); // Centralizar os componentes
        gameOverPanel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0; // Posição na coluna
        gbc.gridy = GridBagConstraints.RELATIVE; 
        gbc.anchor = GridBagConstraints.CENTER; 

        gameOverLabel = new JLabel("Game Over!");
        gameOverLabel.setForeground(Color.WHITE);
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 40));

        restartLabel = new JLabel("Pressione espaço para reiniciar!");
        restartLabel.setForeground(Color.WHITE);
        restartLabel.setFont(new Font("Arial", Font.BOLD, 20));

        gameOverPanel.add(gameOverLabel, gbc);
        gameOverPanel.add(restartLabel, gbc);

        this.add(gameOverPanel, BorderLayout.CENTER);

        startGame();

    }

    // Método para iniciar o jogo
    private void startGame() {
        gameOverPanel.setVisible(false);
        snake.clear(); 
        snake.add(new Point(WIDTH / 2, HEIGHT / 2));
        direction = ' ';
        running = true; 
        timer = new Timer(100, this);
        timer.start();
        spawnFood(); 
    }
    // Método que gera a posição aleatória da comida
    private void spawnFood() {
        Random random = new Random(); 
        Point newFood;

        do {
            int x = random.nextInt(WIDTH / TILE_SIZE) * TILE_SIZE; 
            int y = random.nextInt(HEIGHT / TILE_SIZE) * TILE_SIZE;
            newFood = new Point(x, y);
        } while(snake.contains(newFood));
        
        food = newFood;
        
    }

    //Método que desenha a cobra e a comida
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Chama o método da superclasse para limpar o painel

        g.setColor(Color.BLACK);
        for( Point p : snake ){
            g.fillRect(p.x, p.y, TILE_SIZE, TILE_SIZE);
        }
        g.setColor(Color.MAGENTA);
        g.fillRect(snake.get(0).x, snake.get(0).y, TILE_SIZE, TILE_SIZE);
        
        g.setColor(Color.RED);
        g.fillRect(food.x, food.y, TILE_SIZE, TILE_SIZE);
        if (!running) {
            showGameOver(g); 
        }
    }
    // Método que abre o painel de game over
    private void showGameOver(Graphics g) {
        gameOverPanel.setVisible(true);
    }
    // Método da interface ActionListener para responder a eventos na tela
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) { 
            moveSnake(); 
            checkCollision(); 
            repaint(); // Repaint o painel para atualizar a tela
        }
    }
    // Método responsável por mover a cobra 
    private void moveSnake() {
        Point head = snake.get(0);
        Point newHead = new Point(head);
        switch (direction) {
            case 'U' -> newHead.y -= TILE_SIZE; // Move para cima
            case 'D' -> newHead.y += TILE_SIZE; // Move para baixo
            case 'L' -> newHead.x -= TILE_SIZE; // Move para a esquerda
            case 'R' -> newHead.x += TILE_SIZE; // Move para a direita
        }
        snake.add(0, newHead); // Adiciona a nova cabeça na frente da lista
        if (newHead.equals(food)) { 
            spawnFood();
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    // Verifica se a cabeça colidiu com as bordas ou com o próprio corpo
    private void checkCollision() {
        Point head = snake.get(0);
        if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT || snake.subList(1, snake.size()).contains(head)) {
            running = false; 
            timer.stop();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Jogo da Cobrinha"); // Cria uma nova janela
        SnakeGame game = new SnakeGame(); // Cria uma instância do jogo
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}