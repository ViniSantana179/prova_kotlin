package com.example.prova_01

class Estoque {
    companion object {
        private val listaProdutos = Produtos.listaDeProdutos

        fun calcularValorTotalEstoque(): Float {
            return listaProdutos.sumOf { produto ->
                val preco = produto.preco.toDoubleOrNull() ?: 0.0
                val qtdEstoque = produto.qtdEstoque
                preco * qtdEstoque
            }.toFloat()
        }

        fun calcularQuantidadeTotalProdutos(): Int {
            return listaProdutos.sumOf { produto ->
                produto.qtdEstoque
            }
        }
    }
}
