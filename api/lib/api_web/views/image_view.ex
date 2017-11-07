defmodule ApiWeb.ImageView do
  use ApiWeb, :view
  alias ApiWeb.ImageView

  def render("index.json", %{images: images}) do
    %{data: render_many(images, ImageView, "image.json")}
  end

  def render("show.json", %{image: image}) do
    %{data: render_one(image, ImageView, "image.json")}
  end

  def render("image.json", %{image: image}) do
    %{id: image.id,
      image: image.image,
      thumbnail: image.thumbnail,
      sequence: image.sequence}
  end
end
