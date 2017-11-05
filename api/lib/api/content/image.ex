defmodule Api.Content.Image do
  use Ecto.Schema
  import Ecto.Changeset
  alias Api.Content.Image


  schema "images" do
    field :image, :string
    field :post_id, :integer
    field :sequence, :integer
    field :thumbnail, :string
  end

  @doc false
  def changeset(%Image{} = image, attrs) do
    image
    |> cast(attrs, [:image, :thumbnail, :sequence, :post_id])
    |> validate_required([:image, :thumbnail, :sequence, :post_id])
  end
end
