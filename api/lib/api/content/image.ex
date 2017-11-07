defmodule Api.Content.Image do
  use Ecto.Schema
  import Ecto.Changeset
  alias Api.Content.Image


  schema "images" do
    field :image, :string
    field :sequence, :integer
    field :thumbnail, :string
    field :post_id, :id

  end

  @doc false
  def changeset(%Image{} = image, attrs) do
    image
    |> cast(attrs, [:image, :thumbnail, :sequence])
    |> validate_required([:image, :thumbnail, :sequence])
  end
end
