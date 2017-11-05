defmodule Api.Content.Post do
  use Ecto.Schema
  import Ecto.Changeset
  alias Api.Content.Post


  schema "posts" do
    field :caption, :string
    field :title, :string

    timestamps()
  end

  @doc false
  def changeset(%Post{} = post, attrs) do
    post
    |> cast(attrs, [:title, :caption])
    |> validate_required([:title, :caption])
  end
end
