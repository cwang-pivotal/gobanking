require 'spec_helper'

describe 'Product API' do
  let(:teddy_bear_response) do
    {
        'id' => '1',
        'name' => 'TeddyBear',
        'description' => 'Soft and Cuddly',
        'price' => {'money' => 9.99, 'currency' => 'USD'}
    }
  end

  before do
    HTTParty.delete('http://localhost:8080/products')
  end

  describe "POST products" do
    it 'creates a new product' do
      params = {name: 'SickFish', description: "This fish is not lost", price: {money: 100}}
      response = HTTParty.post('http://localhost:8080/products', {:body => params.to_json, :headers => {'Content-Type' => 'application/json' }})

      expect(response.code).to eq 201
      expect(JSON.parse(response.body)).to eq({
                                                  'id' => '2',
                                                  'name' => 'SickFish',
                                                  'description' => 'This fish is not lost',
                                                  'price' => {'money' => 100.0, 'currency' => 'USD'}
                                              })

      expect(HTTParty.get('http://localhost:8080/products/2').code).to eq(200)
      expect(HTTParty.get('http://localhost:8080/products/SickFish').code).to eq(200)
    end
  end

  describe 'GET products' do
    it 'returns product catalog' do
      response = HTTParty.get('http://localhost:8080/products')
      expect(response.code).to eq(200)
      expect(JSON.parse(response.body)).to eq([teddy_bear_response])
    end
  end

  describe 'GET product/:id_or_name' do
    it 'returns the product' do
      response = HTTParty.get('http://localhost:8080/products/1')
      expect(response.code).to eq(200)
      expect(JSON.parse(response.body)).to eq(teddy_bear_response)

      response = HTTParty.get('http://localhost:8080/products/TeddyBear')
      expect(response.code).to eq(200)
      expect(JSON.parse(response.body)).to eq(teddy_bear_response)
    end

    it 'returns 404 if the product does not exist' do
      expect(HTTParty.get('http://localhost:8080/products/1000').code).to eq(404)
      expect(HTTParty.get('http://localhost:8080/products/OneThousand').code).to eq(404)
    end
  end

  describe "DELETE product/:id_or_name" do
    it 'deletes the product by id' do
      expect(HTTParty.delete('http://localhost:8080/products/1').code).to eq(204)
      expect(HTTParty.get('http://localhost:8080/products/1').code).to eq(404)
    end

    it 'deletes the product by name' do
      expect(HTTParty.delete('http://localhost:8080/products/TeddyBear').code).to eq(204)
      expect(HTTParty.get('http://localhost:8080/products/TeddyBear').code).to eq(404)
    end

    it 'returns 404 if the product does not exist' do
      expect(HTTParty.delete('http://localhost:8080/products/KoalaBear').code).to eq(404)
    end
  end
end