defmodule Api.Content.PendingPost do
  use Ecto.Schema
  import Ecto.Changeset
  alias Api.Content.PendingPost


  schema "pending" do
    field :caption, :string
    field :image, :string
    field :title, :string

    timestamps()
  end

  @doc false
  def changeset(%PendingPost{} = pending_post, attrs) do
	pending_post
	|> cast(attrs, [:title, :caption, :image ])
	|> validate_required([:title, :image])
  end
end
