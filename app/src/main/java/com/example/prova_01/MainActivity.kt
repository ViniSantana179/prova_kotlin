package com.example.prova_01

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NavigationApp()
        }
    }
}

@Composable
fun NavigationApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "Cadastro") {
        composable("Cadastro") {
            Cadastro(navController)
        }
        composable("getAllProducts") {
            ListarProdutos(navController)
        }

        composable("details/{produtoJson}") {
            backStackEntry ->
            val produtoJson = backStackEntry.arguments?.getString("produtoJson")
            val produto = Gson().fromJson(produtoJson, Produtos::class.java)
            DetalhesProdutos(navController, produto)
        }

        composable("statistics/{valorTotalJson}/{quantidadeTotalJson}") {
            backStackEntry ->
            val valorTotalJson = backStackEntry.arguments?.getString("valorTotalJson")
            val quantidadeTotalJson = backStackEntry.arguments?.getString("quantidadeTotalJson")
            EstatisticasProdutos(navController, valorTotalJson, quantidadeTotalJson)
        }
    }
}


@Composable
fun Cadastro(navController: NavController) {
    var listaProdutos by remember { mutableStateOf(listOf<Produtos>()) }
    var nome by remember {mutableStateOf(value = "")}
    var categoria by remember {mutableStateOf(value = "")}
    var preco by remember {mutableStateOf(value = "")}
    var qtde by remember {mutableStateOf(value = "")}
    var context = LocalContext.current

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {

        Text(text = "CADASTRO DE PRODUTO")
        Spacer(modifier = Modifier.height(15.dp))

        TextField(value = nome, onValueChange = { nome = it }, label = { Text(text = "Nome: ")})
        TextField(value = categoria, onValueChange = {categoria = it}, label = { Text(text = "Categoria: ")})
        TextField(value = preco, onValueChange = {preco = it}, label = { Text(text = "Preco: ")})
        TextField(value = qtde, onValueChange = {qtde = it}, label = { Text(text = "Quantidade: ")})

            Button(onClick = {
                if (qtde <= 0.toString() || preco <= 0.toString()) {
                    Toast.makeText(context, "Quantidade e preço devem ser maiores que 0", Toast.LENGTH_SHORT).show()
                }

                if (nome.isNotEmpty() && categoria.isNotEmpty() && preco.isNotEmpty() && qtde.isNotEmpty()) {
                    // Criação do produto
                    val produto = Produtos(nome, categoria, preco, qtde.toInt())

                    // Adicionando o produto à lista estática
                    Produtos.adicionarProduto(produto)
                    Toast.makeText(context, "Produto cadastrado com sucesso", Toast.LENGTH_SHORT).show()

                    // Limpando meu input
                    nome = ""
                    categoria = ""
                    preco = ""
                    qtde = ""

                } else {
                    // Exiba uma mensagem de erro ou faça o tratamento necessário
                    Toast.makeText(context, "Existem campos nao preenchidos",  Toast.LENGTH_SHORT).show()
                }
            }) {
                Text(text = "Cadastrar Produto")
            }

            Spacer(modifier = Modifier.height(15.dp))

            Button(onClick = {
                navController.navigate("getAllProducts")
            }) {
                Text(text = "Lista de Produtos")
            }
    }
}


@Composable
fun ListarProdutos(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Lista de Produtos", fontSize = 23.sp, modifier = Modifier.padding(10.dp))

        Spacer(modifier = Modifier.height(15.dp))

        LazyColumn {
            items(Produtos.listaDeProdutos) { produto ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, // Distribui o espaço entre o texto e o botão
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Produto: ${produto.nome} - Quantidade: (${produto.qtdEstoque})")

                    Button(onClick = {
                        val produtoJson = Gson().toJson(produto)
                        navController.navigate("details/$produtoJson")
                    }) {
                        Text(text = "Detalhes")
                    }
                }
            }
        }

        Button(onClick = {
            val valorTotal = Estoque.calcularValorTotalEstoque()
            val quantidadeTotal = Estoque.calcularQuantidadeTotalProdutos()

            // Passando diretamente os valores calculados
            val valorTotalJson = Gson().toJson(valorTotal)
            val quantidadeTotalJson = Gson().toJson(quantidadeTotal)

            navController.navigate("statistics/$valorTotalJson/$quantidadeTotalJson")
        }) {
            Text(text = "Estatisticas")
        }
        
        Spacer(modifier = Modifier.height(15.dp))
        
        Button(onClick = {
            navController.navigate("Cadastro")
        }) {
            Text(text = "Voltar ao Cadastro")
        }
    }
}


@Composable
fun DetalhesProdutos(navController: NavController, produto: Produtos) {
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(text = "DADOS DO PRODUTO [${produto.nome}]", fontSize = 15.sp)
        Spacer(modifier = Modifier.height(15.dp))
        Text(text = "Nome: ${produto.nome}")
        Text(text = "Categoria: ${produto.categoria}")
        Text(text = "Preco R$: ${produto.preco}")
        Text(text = "Quantidade: ${produto.qtdEstoque}")

        Button(onClick = {
            navController.popBackStack() // Volta para Listar Produtos
        }) {
            Text("Voltar para a lista de produtos")
        }
    }
}


@Composable
fun EstatisticasProdutos(navController: NavController, valorTotal: String?, quantidadeTotal: String?) {
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(text = "ESTATISTICAS DO PRODUTO", fontSize = 15.sp)
        Spacer(modifier = Modifier.height(15.dp))

        Text(text = "Valor Total do Estoque R$: $valorTotal")
        Text(text = "Quantidade Total de Produtos: $quantidadeTotal")


        Button(onClick = {
            navController.popBackStack() // Volta para Listar Produtos
        }) {
            Text("Voltar para a lista de produtos")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NavigationApp()
}