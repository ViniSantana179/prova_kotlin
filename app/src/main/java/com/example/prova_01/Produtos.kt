package com.example.prova_01

data class Produtos(var nome: String, var categoria: String, var preco: String, var qtdEstoque: Int) {

    companion object {
        val listaDeProdutos: MutableList<Produtos> = mutableListOf()

        fun adicionarProduto(produto: Produtos) {
            listaDeProdutos.add(produto)
        }
    }
}